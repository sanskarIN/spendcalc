package `in`.sanskar.spendcalc.domain

import `in`.sanskar.spendcalc.domain.model.CalculationInput
import `in`.sanskar.spendcalc.domain.model.CalculationOutcome
import `in`.sanskar.spendcalc.domain.model.ExpenseItem
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorFuzzTest {
    private val engine = CalculatorEngine()

    @Test
    fun `seeded valid inputs remain deterministic and non negative`() {
        val random = Random(0x5A17C)

        repeat(300) { caseIndex ->
            val items = List(random.nextInt(0, 25)) { itemIndex ->
                ExpenseItem(
                    id = "$caseIndex-$itemIndex",
                    name = "Item $itemIndex",
                    amount = moneyFromCents(random.nextLong(0L, 10_000_000L)),
                )
            }
            val input = CalculationInput(
                items = items,
                discountPercent = percent(random.nextInt(0, 100_001)),
                taxPercent = percent(random.nextInt(0, 100_001)),
                tipPercent = percent(random.nextInt(0, 100_001)),
                serviceChargePercent = percent(random.nextInt(0, 100_001)),
                splitCount = random.nextInt(1, 51),
                currencyCode = "inr",
                exchangeRate = BigDecimal.valueOf(random.nextLong(1L, 1_000_001L), 4),
                convertedCurrencyCode = "usd",
            )

            val first = engine.calculate(input)
            val second = engine.calculate(input)

            assertEquals(first, second)
            assertTrue(first is CalculationOutcome.Success)
            val result = (first as CalculationOutcome.Success).result
            assertTrue(result.subtotal >= BigDecimal.ZERO)
            assertTrue(result.total >= BigDecimal.ZERO)
            assertTrue(result.convertedTotal >= BigDecimal.ZERO)
            assertTrue(result.perPerson >= BigDecimal.ZERO)
            assertTrue(result.convertedPerPerson >= BigDecimal.ZERO)
            assertEquals(2, result.total.scale())
            assertEquals(2, result.convertedTotal.scale())
            assertEquals(2, result.perPerson.scale())
            assertEquals(2, result.convertedPerPerson.scale())
            assertEquals("INR", result.currencyCode)
            assertEquals("USD", result.convertedCurrencyCode)
        }
    }

    @Test
    fun `seeded invalid amounts are rejected without throwing`() {
        val random = Random(0xBAD5EED)

        repeat(100) { index ->
            val negative = moneyFromCents(-random.nextLong(1L, 1_000_000L))
            val input = CalculationInput(
                items = listOf(ExpenseItem("bad-$index", "Invalid", negative)),
            )

            assertTrue(engine.calculate(input) is CalculationOutcome.Failure)
        }
    }

    private fun moneyFromCents(cents: Long): BigDecimal =
        BigDecimal.valueOf(cents, 2).setScale(2, RoundingMode.UNNECESSARY)

    private fun percent(basisPoints: Int): BigDecimal =
        BigDecimal.valueOf(basisPoints.toLong(), 2)
}
