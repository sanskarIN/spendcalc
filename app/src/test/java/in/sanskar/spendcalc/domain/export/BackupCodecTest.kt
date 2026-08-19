package `in`.sanskar.spendcalc.domain.export

import `in`.sanskar.spendcalc.domain.model.AutoDeleteHistory
import `in`.sanskar.spendcalc.domain.model.CalculationTemplate
import `in`.sanskar.spendcalc.domain.model.HistoryRecord
import `in`.sanskar.spendcalc.domain.model.SpendCalcBackup
import `in`.sanskar.spendcalc.domain.model.ThemeMode
import `in`.sanskar.spendcalc.domain.model.UserPreferences
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupCodecTest {
    private val codec = BackupCodec()

    @Test
    fun `round trips history templates preferences and unicode safely`() {
        val backup = SpendCalcBackup(
            exportedAtEpochMillis = 123456L,
            history = listOf(
                HistoryRecord(
                    id = "history-1",
                    createdAtEpochMillis = 10L,
                    label = "Lunch\tनमस्ते\nreceipt",
                    currencyCode = "INR",
                    convertedCurrencyCode = "USD",
                    subtotal = BigDecimal("100.00"),
                    discountAmount = BigDecimal("5.00"),
                    taxAmount = BigDecimal("17.10"),
                    tipAmount = BigDecimal("0.00"),
                    serviceChargeAmount = BigDecimal("0.00"),
                    total = BigDecimal("112.10"),
                    convertedTotal = BigDecimal("1.35"),
                    perPerson = BigDecimal("56.05"),
                    convertedPerPerson = BigDecimal("0.68"),
                    splitCount = 2,
                ),
            ),
            templates = listOf(
                CalculationTemplate(
                    id = "template-1",
                    name = "Dinner\tshared",
                    createdAtEpochMillis = 20L,
                    discountPercent = BigDecimal("5"),
                    taxPercent = BigDecimal("18"),
                    tipPercent = BigDecimal("3"),
                    serviceChargePercent = BigDecimal("2"),
                    splitCount = 4,
                    currencyCode = "INR",
                    exchangeRate = BigDecimal("0.012"),
                    convertedCurrencyCode = "USD",
                ),
            ),
            preferences = UserPreferences(
                themeMode = ThemeMode.DARK,
                largeText = true,
                reducedMotion = true,
                autoDeleteHistory = AutoDeleteHistory.DAYS_90,
                onboardingCompleted = true,
            ),
        )

        val decoded = codec.decode(codec.encode(backup))

        assertTrue(decoded is BackupDecodeResult.Success)
        assertEquals(backup, (decoded as BackupDecodeResult.Success).backup)
    }

    @Test
    fun `detects payload tampering through checksum`() {
        val backup = SpendCalcBackup(
            exportedAtEpochMillis = 1L,
            history = emptyList(),
            templates = emptyList(),
            preferences = UserPreferences(onboardingCompleted = true),
        )
        val encoded = codec.encode(backup)
        val tampered = encoded.replace("SYSTEM", "DARK")

        val decoded = codec.decode(tampered)

        assertEquals(
            BackupDecodeResult.Failure(BackupDecodeError.CHECKSUM_MISMATCH),
            decoded,
        )
    }

    @Test
    fun `rejects unsupported schema before restoring records`() {
        val backup = SpendCalcBackup(
            exportedAtEpochMillis = 1L,
            history = emptyList(),
            templates = emptyList(),
            preferences = UserPreferences(),
        )
        val encoded = codec.encode(backup)
        val body = encoded.substringBeforeLast("SHA256\t")
        val unsupportedHeader = body.replaceFirst("SPENDCALC_BACKUP\t1\t", "SPENDCALC_BACKUP\t2\t")
        val invalidChecksumPayload = unsupportedHeader + "SHA256\tdeadbeef\n"

        val decoded = codec.decode(invalidChecksumPayload)

        assertEquals(
            BackupDecodeResult.Failure(BackupDecodeError.CHECKSUM_MISMATCH),
            decoded,
        )
    }
}
