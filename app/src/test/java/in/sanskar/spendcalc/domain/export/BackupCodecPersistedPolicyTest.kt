package `in`.sanskar.spendcalc.domain.export

import `in`.sanskar.spendcalc.domain.model.CalculationTemplate
import `in`.sanskar.spendcalc.domain.model.HistoryRecord
import `in`.sanskar.spendcalc.domain.model.MAX_SAVED_ID_CHARS
import `in`.sanskar.spendcalc.domain.model.SpendCalcBackup
import `in`.sanskar.spendcalc.domain.model.UserPreferences
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64
import org.junit.Assert.assertEquals
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
    fun `decode rejects non canonical history currency`() {
        val payload = codec.encode(backup(history = listOf(history())))
        val tampered = replaceTextFieldAndResign(payload, "INR", "inr")

        assertEquals(
            BackupDecodeResult.Failure(BackupDecodeError.INVALID_RECORD),
            codec.decode(tampered),
        )
    }

    @Test
    fun `decode rejects non canonical template currency`() {
        val payload = codec.encode(backup(templates = listOf(template())))
        val tampered = replaceTextFieldAndResign(payload, "INR", "inr")

        assertEquals(
            BackupDecodeResult.Failure(BackupDecodeError.INVALID_RECORD),
            codec.decode(tampered),
        )
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

    private fun replaceTextFieldAndResign(payload: String, from: String, to: String): String {
        val body = payload.substringBeforeLast("SHA256\t")
        val updatedBody = body.replaceFirst(encoded(from), encoded(to))
        require(updatedBody != body) { "Expected encoded field was not present" }
        return updatedBody + "SHA256\t${sha256(updatedBody)}\n"
    }

    private fun encoded(value: String): String =
        Base64.getUrlEncoder().withoutPadding().encodeToString(value.toByteArray(StandardCharsets.UTF_8))

    private fun sha256(value: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(StandardCharsets.UTF_8))
            .joinToString("") { byte -> "%02x".format(byte.toInt() and 0xff) }

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
