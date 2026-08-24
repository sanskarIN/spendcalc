package `in`.sanskar.spendcalc.domain.export

import `in`.sanskar.spendcalc.domain.model.HistoryRecord
import `in`.sanskar.spendcalc.domain.model.MAX_SAVED_NAME_CHARS
import `in`.sanskar.spendcalc.domain.model.SpendCalcBackup
import `in`.sanskar.spendcalc.domain.model.UserPreferences
import `in`.sanskar.spendcalc.domain.model.isWellFormedUtf16
import `in`.sanskar.spendcalc.domain.model.normalizeSavedName
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupCodecSavedNamePolicyTest {
    private val codec = BackupCodec()

    @Test
    fun `unicode boundary saved label remains exportable and round trips exactly`() {
        val rawLabel = "x".repeat(MAX_SAVED_NAME_CHARS - 1) + "😀" + "tail"
        val label = normalizeSavedName(rawLabel, "Calculation")
        val backup = SpendCalcBackup(
            exportedAtEpochMillis = 1L,
            history = listOf(history(label)),
            templates = emptyList(),
            preferences = UserPreferences(onboardingCompleted = true),
        )

        val decoded = codec.decode(codec.encode(backup))

        assertTrue(isWellFormedUtf16(label))
        assertTrue(decoded is BackupDecodeResult.Success)
        assertEquals(backup, (decoded as BackupDecodeResult.Success).backup)
    }

    private fun history(label: String) = HistoryRecord(
        id = "unicode-boundary",
        createdAtEpochMillis = 1L,
        label = label,
        currencyCode = "INR",
        convertedCurrencyCode = "INR",
        subtotal = BigDecimal("1.00"),
        discountAmount = BigDecimal.ZERO,
        taxAmount = BigDecimal.ZERO,
        tipAmount = BigDecimal.ZERO,
        serviceChargeAmount = BigDecimal.ZERO,
        total = BigDecimal("1.00"),
        convertedTotal = BigDecimal("1.00"),
        perPerson = BigDecimal("1.00"),
        convertedPerPerson = BigDecimal("1.00"),
        splitCount = 1,
    )
}
