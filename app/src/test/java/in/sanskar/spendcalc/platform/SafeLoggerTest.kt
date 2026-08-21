package `in`.sanskar.spendcalc.platform

import java.util.Locale
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SafeLoggerTest {
    @Test
    fun `redacts blocked structured fields`() {
        val message = SafeLogger.format(
            event = "export_failed",
            fields = mapOf(
                "token" to "real-secret-value",
                "backup" to "private backup data",
                "stage" to "share",
            ),
        )

        assertFalse(message.contains("real-secret-value"))
        assertFalse(message.contains("private backup data"))
        assertTrue(message.contains("token=[REDACTED]"))
        assertTrue(message.contains("backup=[REDACTED]"))
        assertTrue(message.contains("stage=share"))
    }

    @Test
    fun `redaction keys are locale independent`() {
        val previousLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"))
            val message = SafeLogger.format(
                event = "event",
                fields = mapOf("API_KEY" to "must-not-leak"),
            )

            assertFalse(message.contains("must-not-leak"))
            assertTrue(message.contains("API_KEY=[REDACTED]"))
        } finally {
            Locale.setDefault(previousLocale)
        }
    }

    @Test
    fun `removes line breaks from event metadata`() {
        val message = SafeLogger.format("event\nname", mapOf("state" to "one\ntwo"))

        assertFalse(message.contains('\n'))
        assertTrue(message.contains("event name"))
        assertTrue(message.contains("state=one two"))
    }
}
