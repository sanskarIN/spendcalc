package `in`.sanskar.spendcalc.domain.export

import `in`.sanskar.spendcalc.domain.model.CalculationTemplate
import `in`.sanskar.spendcalc.domain.model.HistoryRecord
import `in`.sanskar.spendcalc.domain.model.MAX_SAVED_ID_CHARS
import `in`.sanskar.spendcalc.domain.model.SpendCalcBackup
import `in`.sanskar.spendcalc.domain.model.UserPreferences
import java.math.BigDecimal
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupCodecPersistedPolicyTest {
    private val codec = BackupCodec()

    @Test
    fun `encode rejects non canonical template currency`() {
        val backup = backup(templates = listOf(template().copy(currencyCode = "inr")))

        assertTrue(runCatching { codec.encode(backup) }.isFailure)
    }

    @Test
    fun `encode rejects invalid history envelope`() {
        val backup = backup(
            history = listOf(
                history().copy(
                    id = "x".repeat(MAX_SAVED_ID_CHARS + 1),
                ),
            ),
        )

        assertTrue(runCatching { codec.encode(backup) }.isFailure)
    }

    @Test
    fun `encode rejects negative template timestamp`() {
        val backup = backup(templates = listOf(template().copy(createdAtEpochMillis = -1L)))

        assertTrue(runCatching { codec.encode(backup) }.isFailure)
    }

    private fun backup(
        history: List<HistoryRecord> = emptyList(),
        templates: List<CalculationTemplate> = emptyList(),
    ) = SpendCalcBackup(
        exportedAtEpochMillis = 1L,
        history = history,
        templates = templates,
        preferences = UserPreferences(onboardingCompleted = true),
    )

    private fun history() = HistoryRecord(
        id = "history-1",
        createdAtEpochMillis = 1L,
        label = "Dinner",
        currencyCode = "INR",
        convertedCurrencyCode = "USD",
        subtotal = BigDecimal("100.00"),
        discountAmount = BigDecimal.ZERO,
        taxAmount = BigDecimal("18.00"),
        tipAmount = BigDecimal.ZERO,
        serviceChargeAmount = BigDecimal.ZERO,
        total = BigDecimal("118.00"),
        convertedTotal = BigDecimal("1.42"),
        perPerson = BigDecimal("59.00"),
        convertedPerPerson = BigDecimal("0.71"),
        splitCount = 2,
    )

    private fun template() = CalculationTemplate(
        id = "template-1",
        name = "Dinner preset",
        createdAtEpochMillis = 1L,
        discountPercent = BigDecimal.ZERO,
        taxPercent = BigDecimal("18"),
        tipPercent = BigDecimal.ZERO,
        serviceChargePercent = BigDecimal.ZERO,
        splitCount = 2,
        currencyCode = "INR",
        exchangeRate = BigDecimal("0.012"),
        convertedCurrencyCode = "USD",
    )
}
