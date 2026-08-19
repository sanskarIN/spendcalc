# Logging

SpendCalc deliberately keeps production logging minimal because calculation labels, receipt contents, export payloads, backup data, filenames/URIs, and settings context can contain user information.

`app/src/main/java/in/sanskar/spendcalc/platform/SafeLogger.kt` is the structured logging boundary for events that genuinely need diagnostics. Its regression coverage is in `SafeLoggerTest.kt`; both files are mapped in [`codebase-reference.md`](codebase-reference.md).

## Rules

- Log stable event metadata, not user content.
- Never log receipt text, history/template names, backup contents, document contents, tokens, authorization values, cookies, passwords, API keys, signing material, or secrets.
- Do not log raw file/document URIs when they may expose user-selected provider paths/account identifiers; prefer a stable operation/stage category.
- Common sensitive field names are automatically replaced with `[REDACTED]`.
- Sensitive-key matching is normalized with `Locale.ROOT` so redaction behavior does not change under locales such as Turkish.
- Values are line-break sanitized and length-bounded before reaching Android Logcat.
- Do not use `println`, direct `Log.*`, or exception dumps containing payloads for new production diagnostics when `SafeLogger` can express the event safely.
- Release logs should be sparse, actionable, and safe if included in a sanitized bug report.

## Good event shape

```kotlin
SafeLogger.warning(
    event = "export_failed",
    fields = mapOf("format" to "pdf", "stage" to "share"),
)
```

The event says what operation failed and where without including receipt text, an export path, or private user content.

## Exceptions

Do not add raw exception messages unless they have been reviewed for user-data leakage. Prefer:

- a stable error category;
- exception class/type when it is safe and useful;
- operation stage;
- bounded non-sensitive state such as export format.

Never serialize an exception object or stack/context that includes backup payloads, receipt text, labels, secret configuration, or signing data into structured fields.

## Backup and restore

Backup decode/restore errors should be represented by stable result/error categories. The parser already exposes bounded typed decode errors; logging should not dump the rejected payload or decoded fields.

Restore rollback failures are security/reliability diagnostics, but logging them still does not justify recording the prior/current backup contents or preferences snapshot.

## External actions

Opening GitHub, funding, or email applications is user-triggered. If diagnostics are ever needed, log the action category rather than raw mail body/query/provider details. Core operation remains local-first even though the selected external app may use network access.

## Testing

`SafeLoggerTest` verifies redaction, newline sanitization, and locale-independent sensitive-key handling. Any new blocked category or change to key normalization should receive a regression assertion.

Logging-policy changes should also review [`SECURITY.md`](../SECURITY.md), [`PRIVACY.md`](../PRIVACY.md), and the documentation change matrix in [`documentation-map.md`](documentation-map.md) when they affect public privacy/security claims.
