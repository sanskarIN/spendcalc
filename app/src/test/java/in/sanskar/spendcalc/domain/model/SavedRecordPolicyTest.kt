package `in`.sanskar.spendcalc.domain.model

import java.math.BigDecimal
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SavedRecordPolicyTest {
    @Test
    fun `valid history record satisfies persisted envelope`() {
        assertTrue(isValidHistoryRecord(historyRecord()))
    }

    @Test
    fun `history record rejects non canonical currency`() {
        assertFalse(isValidHistoryRecord(historyRecord().copy(currencyCode = "inr")))
    }

    @Test
    fun `history record rejects unsupported result magnitude`() {
        assertFalse(
            isValidHistoryRecord(
                historyRecord().copy(
                    convertedTotal = BigDecimal("1" + "0".repeat(MAX_SAVED_RESULT_INTEGER_DIGITS)),
                ),
            ),
        )
    }

    @Test
    fun `history record rejects invalid split count`() {
        assertFalse(isValidHistoryRecord(historyRecord().copy(splitCount = 0)))
        assertFalse(isValidHistoryRecord(historyRecord().copy(splitCount = MAX_SAVED_SPLIT_COUNT + 1)))
    }

    @Test
    fun `saved id rejects malformed or oversized values`() {
        assertFalse(isValidSavedId(""))
        assertFalse(isValidSavedId("x".repeat(MAX_SAVED_ID_CHARS + 1)))
        assertFalse(isValidSavedId("broken\uD83D"))
    }

    @Test
    fun `template envelope requires canonical currencies and nonnegative timestamp`() {
        val valid = template()
        assertTrue(isValidTemplateEnvelope(valid))
        assertFalse(isValidTemplateEnvelope(valid.copy(currencyCode = "usd")))
        assertFalse(isValidTemplateEnvelope(valid.copy(createdAtEpochMillis = -1L)))
    }

    private fun historyRecord() = HistoryRecord(
        id = "history-1",
        createdAtEpochMillis = 1L,
        label = "Dinner",
        currencyCode = "INR",
        convertedCurrencyCode = "USD",
        subtotal = BigDecimal("100.00"),
        discountAmount = BigDecimal("5.00"),
        taxAmount = BigDecimal("18.00"),
        tipAmount = BigDecimal("2.00"),
        serviceChargeAmount = BigDecimal("1.00"),
        total = BigDecimal("116.00"),
        convertedTotal = BigDecimal("1.40"),
        perPerson = BigDecimal("58.00"),
        convertedPerPerson = BigDecimal("0.70"),
        splitCount = 2,
    )

    private fun template() = CalculationTemplate(
        id = "template-1",
        name = "Dinner preset",
        createdAtEpochMillis = 1L,
        discountPercent = BigDecimal("5"),
        taxPercent = BigDecimal("18"),
        tipPercent = BigDecimal("2"),
        serviceChargePercent = BigDecimal("1"),
        splitCount = 2,
        currencyCode = "INR",
        exchangeRate = BigDecimal("0.012"),
        convertedCurrencyCode = "USD",
    )
}
