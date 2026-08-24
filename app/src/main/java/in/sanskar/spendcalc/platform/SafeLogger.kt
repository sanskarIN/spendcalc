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
    private val blockedKeyFragments = setOf(
        "password",
        "passcode",
        "token",
        "authorization",
        "cookie",
        "secret",
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
                val safeValue = if (isBlockedKey(key)) {
                    REDACTED
                } else {
                    sanitize(value?.toString().orEmpty())
                }
                "${sanitize(key)}=$safeValue"
            }
        return "$safeEvent $encoded"
    }

    private fun isBlockedKey(key: String): Boolean {
        val compactKey = key
            .trim()
            .lowercase(Locale.ROOT)
            .filter(Char::isLetterOrDigit)
        return blockedKeyFragments.any { fragment -> compactKey.contains(fragment) }
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
