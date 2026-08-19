package `in`.sanskar.spendcalc.platform

import android.util.Log
import java.util.Locale

/**
 * Minimal structured logger that refuses common sensitive field names and truncates values.
 *
 * SpendCalc intentionally does not log calculation labels, receipt contents, exported payloads,
 * credentials, or other user data. Callers should log event metadata only.
 */
object SafeLogger {
    private val blockedKeys = setOf(
        "password",
        "passcode",
        "token",
        "authorization",
        "cookie",
        "secret",
        "api_key",
        "apikey",
        "receipt",
        "label",
        "backup",
        "payload",
    )

    fun info(event: String, fields: Map<String, Any?> = emptyMap()) {
        Log.i(TAG, format(event, fields))
    }

    fun warning(event: String, fields: Map<String, Any?> = emptyMap()) {
        Log.w(TAG, format(event, fields))
    }

    internal fun format(event: String, fields: Map<String, Any?>): String {
        val safeEvent = sanitize(event).ifBlank { "event" }
        if (fields.isEmpty()) return safeEvent
        val encoded = fields.entries
            .sortedBy { it.key }
            .joinToString(separator = " ") { (key, value) ->
                val normalizedKey = key.trim().lowercase(Locale.ROOT)
                val safeValue = if (normalizedKey in blockedKeys) {
                    REDACTED
                } else {
                    sanitize(value?.toString().orEmpty())
                }
                "${sanitize(key)}=$safeValue"
            }
        return "$safeEvent $encoded"
    }

    private fun sanitize(value: String): String =
        value
            .replace('\n', ' ')
            .replace('\r', ' ')
            .replace('\t', ' ')
            .take(MAX_VALUE_LENGTH)

    private const val TAG = "SpendCalc"
    private const val REDACTED = "[REDACTED]"
    private const val MAX_VALUE_LENGTH = 160
}
