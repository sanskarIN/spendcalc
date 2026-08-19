package in.sanskar.spendcalc.domain.export

import in.sanskar.spendcalc.domain.CalculatorEngine
import in.sanskar.spendcalc.domain.model.CalculationInput
import in.sanskar.spendcalc.domain.model.CalculationOutcome
import in.sanskar.spendcalc.domain.model.ExpenseItem
import java.math.BigDecimal
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CsvExportFormatterTest {
    private val formatter = CsvExportFormatter()

    @Test
    fun `quotes values and neutralizes spreadsheet formulas in item names`() {
        val input = CalculationInput(
            items = listOf(ExpenseItem("1", "=2+2, suspicious", BigDecimal("5.25"))),
        )
        val result = (CalculatorEngine().calculate(input) as CalculationOutcome.Success).result

        val csv = formatter.format(input, result)

        assertTrue(csv.contains("\"'=2+2, suspicious\""))
        assertFalse(csv.contains("\"=2+2, suspicious\""))
        assertTrue(csv.contains("\"total\",\"5.25\",\"INR\""))
    }

    @Test
    fun `escapes embedded quotes`() {
        val input = CalculationInput(
            items = listOf(ExpenseItem("1", "A \"quoted\" item", BigDecimal.ONE)),
        )
        val result = (CalculatorEngine().calculate(input) as CalculationOutcome.Success).result

        val csv = formatter.format(input, result)

        assertTrue(csv.contains("A \"\"quoted\"\" item"))
    }
}
