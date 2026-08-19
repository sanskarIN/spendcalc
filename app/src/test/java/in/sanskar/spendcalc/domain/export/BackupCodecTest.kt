package `in`.sanskar.spendcalc.domain.export

import `in`.sanskar.spendcalc.domain.CalculatorEngine
import `in`.sanskar.spendcalc.domain.model.AutoDeleteHistory
import `in`.sanskar.spendcalc.domain.model.CalculationInput
import `in`.sanskar.spendcalc.domain.model.CalculationOutcome
import `in`.sanskar.spendcalc.domain.model.CalculationTemplate
import `in`.sanskar.spendcalc.domain.model.ExpenseItem
import `in`.sanskar.spendcalc.domain.model.HistoryRecord
import `in`.sanskar.spendcalc.domain.model.SpendCalcBackup
import `in`.sanskar.spendcalc.domain.model.ThemeMode
import `in`.sanskar.spendcalc.domain.model.UserPreferences
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupCodecTest {
    private val codec = BackupCodec()

    @Test
    fun `round trips history templates preferences and unicode safely`() {
        val backup = SpendCalcBackup(
            exportedAtEpochMillis = 123456L,
            history = listOf(history("history-1", 10L, "Lunch\tनमस्ते\nreceipt")),
            templates = listOf(template("template-1", 20L, "Dinner\tshared")),
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
    fun `round trips maximum range result produced by the calculator`() {
        val outcome = CalculatorEngine().calculate(
            CalculationInput(
                items = List(100) { index ->
                    ExpenseItem(
                        id = "item-$index",
                        name = "Item $index",
                        amount = BigDecimal("999999999999999"),
                    )
                },
                taxPercent = BigDecimal("1000"),
                tipPercent = BigDecimal("1000"),
                serviceChargePercent = BigDecimal("1000"),
                exchangeRate = BigDecimal("999999999999999"),
                currencyCode = "INR",
                convertedCurrencyCode = "USD",
            ),
        )
        val result = (outcome as CalculationOutcome.Success).result
        assertTrue(result.convertedTotal.precision() - result.convertedTotal.scale() > 15)
        val record = HistoryRecord(
            id = "large-valid-result",
            createdAtEpochMillis = 10L,
            label = "Maximum valid calculation",
            currencyCode = result.currencyCode,
            convertedCurrencyCode = result.convertedCurrencyCode,
            subtotal = result.subtotal,
            discountAmount = result.discountAmount,
            taxAmount = result.taxAmount,
            tipAmount = result.tipAmount,
            serviceChargeAmount = result.serviceChargeAmount,
            total = result.total,
            convertedTotal = result.convertedTotal,
            perPerson = result.perPerson,
            convertedPerPerson = result.convertedPerPerson,
            splitCount = result.splitCount,
        )
        val backup = emptyBackup().copy(history = listOf(record))

        val decoded = codec.decode(codec.encode(backup))

        assertEquals(backup, (decoded as BackupDecodeResult.Success).backup)
    }

    @Test
    fun `rejects history decimal above bounded calculated result magnitude`() {
        val tooLarge = history("too-large", 10L, "Too large").copy(
            convertedTotal = BigDecimal("10000000000000000000000000000000000"),
        )

        assertTrue(runCatching { codec.encode(emptyBackup().copy(history = listOf(tooLarge))) }.isFailure)
    }

    @Test
    fun `rejects saved names that exceed the application bound`() {
        val oversizedHistory = history("history", 10L, "x".repeat(121))
        val oversizedTemplate = template("template", 20L, "y".repeat(121))

        assertTrue(runCatching { codec.encode(emptyBackup().copy(history = listOf(oversizedHistory))) }.isFailure)
        assertTrue(runCatching { codec.encode(emptyBackup().copy(templates = listOf(oversizedTemplate))) }.isFailure)
    }

    @Test
    fun `rejects malformed utf8 from a checksummed text field`() {
        val encoded = codec.encode(emptyBackup().copy(history = listOf(history("one", 10L, "One"))))
        val historyLine = encoded.lineSequence().first { it.startsWith("H\t") }
        val fields = historyLine.split('\t').toMutableList()
        fields[1] = "_w"
        val malformedLine = fields.joinToString("\t")
        val body = encoded.substringBeforeLast("SHA256\t")
            .replace("$historyLine\n", "$malformedLine\n")
        val payload = body + "SHA256\t${sha256(body)}\n"

        assertEquals(
            BackupDecodeResult.Failure(BackupDecodeError.INVALID_RECORD),
            codec.decode(payload),
        )
    }

    @Test
    fun `detects payload tampering through checksum`() {
        val backup = emptyBackup()
        val encoded = codec.encode(backup)
        val tampered = encoded.replace("SYSTEM", "DARK")

        val decoded = codec.decode(tampered)

        assertEquals(
            BackupDecodeResult.Failure(BackupDecodeError.CHECKSUM_MISMATCH),
            decoded,
        )
    }

    @Test
    fun `rejects unsupported schema with a valid checksum`() {
        val encoded = codec.encode(emptyBackup())
        val body = encoded.substringBeforeLast("SHA256\t")
            .replaceFirst("SPENDCALC_BACKUP\t1\t", "SPENDCALC_BACKUP\t2\t")
        val payload = body + "SHA256\t${sha256(body)}\n"

        val decoded = codec.decode(payload)

        assertEquals(
            BackupDecodeResult.Failure(BackupDecodeError.UNSUPPORTED_VERSION),
            decoded,
        )
    }

    @Test
    fun `rejects duplicate identifiers before export`() {
        val duplicateHistory = history("same", 10L, "A")
        val backup = emptyBackup().copy(history = listOf(duplicateHistory, duplicateHistory.copy(label = "B")))

        assertTrue(runCatching { codec.encode(backup) }.isFailure)
    }

    @Test
    fun `rejects negative export timestamps before export`() {
        assertTrue(runCatching { codec.encode(emptyBackup().copy(exportedAtEpochMillis = -1L)) }.isFailure)
    }

    @Test
    fun `rejects exponent shaped decimals before rendering an export`() {
        val unsafe = history("huge", 10L, "Unsafe").copy(subtotal = BigDecimal("1E+1000"))

        assertTrue(runCatching { codec.encode(emptyBackup().copy(history = listOf(unsafe))) }.isFailure)
    }

    @Test
    fun `rejects exponent shaped decimals from checksummed input`() {
        val encoded = codec.encode(emptyBackup().copy(history = listOf(history("one", 10L, "One"))))
        val body = encoded.substringBeforeLast("SHA256\t")
            .replaceFirst("\t100.00\t", "\t1E+1000\t")
        val payload = body + "SHA256\t${sha256(body)}\n"

        assertEquals(
            BackupDecodeResult.Failure(BackupDecodeError.INVALID_RECORD),
            codec.decode(payload),
        )
    }

    @Test
    fun `rejects duplicate identifiers from a checksummed payload`() {
        val first = history("same", 10L, "A")
        val second = first.copy(label = "B", createdAtEpochMillis = 20L)
        val encoded = codec.encode(emptyBackup().copy(history = listOf(first)))
        val firstHistoryLine = encoded.lineSequence().first { it.startsWith("H\t") }
        val secondEncoded = codec.encode(emptyBackup().copy(history = listOf(second)))
        val secondHistoryLine = secondEncoded.lineSequence().first { it.startsWith("H\t") }
        val body = encoded.substringBeforeLast("SHA256\t")
            .replace("$firstHistoryLine\n", "$firstHistoryLine\n$secondHistoryLine\n")
        val payload = body + "SHA256\t${sha256(body)}\n"

        val decoded = codec.decode(payload)

        assertEquals(
            BackupDecodeResult.Failure(BackupDecodeError.INVALID_RECORD),
            decoded,
        )
    }

    private fun emptyBackup() = SpendCalcBackup(
        exportedAtEpochMillis = 1L,
        history = emptyList(),
        templates = emptyList(),
        preferences = UserPreferences(onboardingCompleted = true),
    )

    private fun history(id: String, createdAt: Long, label: String) = HistoryRecord(
        id = id,
        createdAtEpochMillis = createdAt,
        label = label,
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
    )

    private fun template(id: String, createdAt: Long, name: String) = CalculationTemplate(
        id = id,
        name = name,
        createdAtEpochMillis = createdAt,
        discountPercent = BigDecimal("5"),
        taxPercent = BigDecimal("18"),
        tipPercent = BigDecimal("3"),
        serviceChargePercent = BigDecimal("2"),
        splitCount = 4,
        currencyCode = "INR",
        exchangeRate = BigDecimal("0.012"),
        convertedCurrencyCode = "USD",
    )

    private fun sha256(value: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(StandardCharsets.UTF_8))
            .joinToString("") { byte -> "%02x".format(byte.toInt() and 0xff) }
}
