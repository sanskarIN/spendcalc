package in.sanskar.spendcalc.domain.export

import in.sanskar.spendcalc.domain.CalculatorEngine
import in.sanskar.spendcalc.domain.model.CalculationInput
import in.sanskar.spendcalc.domain.model.CalculationOutcome
import in.sanskar.spendcalc.domain.model.ExpenseItem
import java.math.BigDecimal
import org.junit.Assert.assertTrue
import org.junit.Test

class ReceiptTextFormatterTest {
    @Test
    fun `includes totals split and project credit`() {
        val input = CalculationInput(
            items = listOf(ExpenseItem("1", "Lunch", BigDecimal("120"))),
            splitCount = 3,
        )
        val result = (CalculatorEngine().calculate(input) as CalculationOutcome.Success).result

        val receipt = ReceiptTextFormatter().format(input, result)

        assertTrue(receipt.contains("Lunch: INR 120"))
        assertTrue(receipt.contains("Total: INR 120.00"))
        assertTrue(receipt.contains("Per person: INR 40.00"))
        assertTrue(receipt.contains("Made by the Sanskar"))
    }
}
