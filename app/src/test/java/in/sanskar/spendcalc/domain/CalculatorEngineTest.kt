package in.sanskar.spendcalc.domain

import in.sanskar.spendcalc.domain.model.CalculationError
import in.sanskar.spendcalc.domain.model.CalculationInput
import in.sanskar.spendcalc.domain.model.CalculationOutcome
import in.sanskar.spendcalc.domain.model.ExpenseItem
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorEngineTest {
    private val engine = CalculatorEngine()

    @Test
    fun `calculates discount charges conversion and split deterministically`() {
        val outcome = engine.calculate(
            CalculationInput(
                items = listOf(
                    ExpenseItem("1", "Food", BigDecimal("100.00")),
                    ExpenseItem("2", "Drink", BigDecimal("50.00")),
                ),
                discountPercent = BigDecimal("10"),
                taxPercent = BigDecimal("10"),
                tipPercent = BigDecimal("5"),
                serviceChargePercent = BigDecimal("2"),
                splitCount = 2,
                currencyCode = "INR",
                exchangeRate = BigDecimal("2"),
                convertedCurrencyCode = "USD",
            ),
        )

        val result = (outcome as CalculationOutcome.Success).result
        assertEquals(BigDecimal("150.00"), result.subtotal)
        assertEquals(BigDecimal("15.00"), result.discountAmount)
        assertEquals(BigDecimal("135.00"), result.taxableBase)
        assertEquals(BigDecimal("13.50"), result.taxAmount)
        assertEquals(BigDecimal("6.75"), result.tipAmount)
        assertEquals(BigDecimal("2.70"), result.serviceChargeAmount)
        assertEquals(BigDecimal("157.95"), result.total)
        assertEquals(BigDecimal("315.90"), result.convertedTotal)
        assertEquals(BigDecimal("78.98"), result.perPerson)
        assertEquals(BigDecimal("157.95"), result.convertedPerPerson)
    }

    @Test
    fun `uses decimal arithmetic rather than floating point`() {
        val outcome = engine.calculate(
            CalculationInput(
                items = listOf(
                    ExpenseItem("1", "A", BigDecimal("0.10")),
                    ExpenseItem("2", "B", BigDecimal("0.20")),
                ),
            ),
        )

        val result = (outcome as CalculationOutcome.Success).result
        assertEquals(BigDecimal("0.30"), result.total)
    }

    @Test
    fun `rounds split values half up`() {
        val outcome = engine.calculate(
            CalculationInput(
                items = listOf(ExpenseItem("1", "A", BigDecimal("10"))),
                splitCount = 3,
            ),
        )

        val result = (outcome as CalculationOutcome.Success).result
        assertEquals(BigDecimal("3.33"), result.perPerson)
    }

    @Test
    fun `accepts lowercase ISO style currency input and normalizes result`() {
        val outcome = engine.calculate(
            CalculationInput(
                items = emptyList(),
                currencyCode = "inr",
                convertedCurrencyCode = "usd",
            ),
        )

        val result = (outcome as CalculationOutcome.Success).result
        assertEquals("INR", result.currencyCode)
        assertEquals("USD", result.convertedCurrencyCode)
    }

    @Test
    fun `rejects negative item amount`() {
        val outcome = engine.calculate(
            CalculationInput(
                items = listOf(ExpenseItem("bad", "Invalid", BigDecimal("-1"))),
            ),
        )

        assertTrue(outcome is CalculationOutcome.Failure)
        val errors = (outcome as CalculationOutcome.Failure).errors
        assertTrue(errors.contains(CalculationError.InvalidAmount("bad")))
    }

    @Test
    fun `rejects zero exchange rate and split count`() {
        val outcome = engine.calculate(
            CalculationInput(
                items = emptyList(),
                exchangeRate = BigDecimal.ZERO,
                splitCount = 0,
            ),
        )

        val errors = (outcome as CalculationOutcome.Failure).errors
        assertTrue(errors.contains(CalculationError.InvalidExchangeRate))
        assertTrue(errors.contains(CalculationError.InvalidSplitCount))
    }

    @Test
    fun `rejects malformed currency codes`() {
        val outcome = engine.calculate(
            CalculationInput(
                items = emptyList(),
                currencyCode = "RUPEES",
                convertedCurrencyCode = "US",
            ),
        )

        val errors = (outcome as CalculationOutcome.Failure).errors
        assertTrue(errors.contains(CalculationError.InvalidCurrencyCode))
        assertTrue(errors.contains(CalculationError.InvalidConvertedCurrencyCode))
    }
}
