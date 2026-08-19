# Logging

SpendCalc deliberately keeps production logging minimal because calculation labels, receipt contents, export payloads, and backup data can contain user information.

`platform/SafeLogger.kt` provides the structured logging boundary for events that genuinely need diagnostics.

## Rules

- Log event metadata, not user content.
- Never log receipt text, backup contents, labels, tokens, authorization values, cookies, passwords, API keys, or secrets.
- Common sensitive field names are automatically replaced with `[REDACTED]`.
- Values are line-break sanitized and length-bounded before reaching Android Logcat.
- Do not use `println`, direct `Log.*`, or exception dumps containing payloads for new production diagnostics when `SafeLogger` can express the event safely.
- Release logs should be sparse and actionable.

## Example

```kotlin
SafeLogger.warning(
    event = "export_failed",
    fields = mapOf("format" to "pdf", "stage" to "share"),
)
```

Do not add raw exception messages unless they have been reviewed for user-data leakage. Prefer a stable error category or exception class name.

## Testing

`SafeLoggerTest` verifies redaction and newline sanitization. Any new blocked category should receive a regression assertion.
