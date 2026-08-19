package `in`.sanskar.spendcalc.domain.export

import `in`.sanskar.spendcalc.domain.model.CalculationTemplate
import `in`.sanskar.spendcalc.domain.model.HistoryRecord
import `in`.sanskar.spendcalc.domain.model.SpendCalcBackup
import `in`.sanskar.spendcalc.domain.model.UserPreferences
import java.math.BigDecimal
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupCodecFuzzTest {
    @Test
    fun `deterministic unicode labels round trip through record encoding`() {
        val random = Random(15082026)
        val alphabet = listOf('a', 'Z', '0', ' ', '\t', '\n', '₹', 'é', '你', '界', ',', '"', '=')
        val codec = BackupCodec()

        repeat(100) { iteration ->
            val label = buildString {
                repeat(random.nextInt(0, 80)) {
                    append(alphabet[random.nextInt(alphabet.size)])
                }
            }
            val backup = SpendCalcBackup(
                exportedAtEpochMillis = iteration.toLong(),
                history = listOf(
                    HistoryRecord(
                        id = "h-$iteration",
                        createdAtEpochMillis = iteration.toLong(),
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
                    ),
                ),
                templates = listOf(
                    CalculationTemplate(
                        id = "t-$iteration",
                        name = label,
                        createdAtEpochMillis = iteration.toLong(),
                        discountPercent = BigDecimal.ZERO,
                        taxPercent = BigDecimal.ZERO,
                        tipPercent = BigDecimal.ZERO,
                        serviceChargePercent = BigDecimal.ZERO,
                        splitCount = 1,
                        currencyCode = "INR",
                        exchangeRate = BigDecimal.ONE,
                        convertedCurrencyCode = "INR",
                    ),
                ),
                preferences = UserPreferences(onboardingCompleted = true),
            )

            val decoded = codec.decode(codec.encode(backup))

            assertTrue(decoded is BackupDecodeResult.Success)
            assertEquals(backup, (decoded as BackupDecodeResult.Success).backup)
        }
    }
}
