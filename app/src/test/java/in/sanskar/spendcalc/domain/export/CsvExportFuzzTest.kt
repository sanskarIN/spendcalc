package `in`.sanskar.spendcalc.domain.export

import `in`.sanskar.spendcalc.domain.CalculatorEngine
import `in`.sanskar.spendcalc.domain.model.CalculationInput
import `in`.sanskar.spendcalc.domain.model.CalculationOutcome
import `in`.sanskar.spendcalc.domain.model.ExpenseItem
import java.math.BigDecimal
import kotlin.random.Random
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CsvExportFuzzTest {
    @Test
    fun `arbitrary deterministic labels remain quoted and cannot start formulas`() {
        val random = Random(20260819)
        val formatter = CsvExportFormatter()
        val dangerousPrefixes = charArrayOf('=', '+', '-', '@')

        repeat(250) { index ->
            val prefix = dangerousPrefixes[index % dangerousPrefixes.size]
            val body = buildString {
                repeat(random.nextInt(0, 40)) {
                    append(
                        listOf('a', 'Z', '0', ',', '"', ' ', '\t', '₹', '你', 'é')
                            [random.nextInt(10)],
                    )
                }
            }
            val label = "$prefix$body"
            val input = CalculationInput(
                items = listOf(
                    ExpenseItem(
                        id = index.toString(),
                        name = label,
                        amount = BigDecimal("1.25"),
                    ),
                ),
            )
            val result = (CalculatorEngine().calculate(input) as CalculationOutcome.Success).result
            val csv = formatter.format(input, result)
            val itemLine = csv.lineSequence().drop(1).first()

            assertTrue(itemLine.startsWith("\"item\","))
            assertFalse(itemLine.contains(",\"$prefix"))
            assertTrue(itemLine.contains(",\"'$prefix"))
        }
    }
}
