package `in`.sanskar.spendcalc.domain.export

import `in`.sanskar.spendcalc.domain.CalculatorEngine
import `in`.sanskar.spendcalc.domain.model.AutoDeleteHistory
import `in`.sanskar.spendcalc.domain.model.CalculationInput
import `in`.sanskar.spendcalc.domain.model.CalculationTemplate
import `in`.sanskar.spendcalc.domain.model.HistoryRecord
import `in`.sanskar.spendcalc.domain.model.SpendCalcBackup
import `in`.sanskar.spendcalc.domain.model.ThemeMode
import `in`.sanskar.spendcalc.domain.model.UserPreferences
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64
import java.util.Locale

sealed interface BackupDecodeResult {
    data class Success(val backup: SpendCalcBackup) : BackupDecodeResult
    data class Failure(val error: BackupDecodeError) : BackupDecodeResult
}

enum class BackupDecodeError {
    TOO_LARGE,
    INVALID_FORMAT,
    UNSUPPORTED_VERSION,
    CHECKSUM_MISMATCH,
    INVALID_RECORD,
}

/**
 * Dependency-free, deterministic backup format for local SpendCalc data.
 *
 * Text fields use URL-safe Base64 so tabs/newlines cannot alter record boundaries. A SHA-256
 * checksum detects accidental corruption; it is an integrity check, not a signature.
 */
class BackupCodec {
    private val calculatorEngine = CalculatorEngine()

    fun encode(backup: SpendCalcBackup): String {
        require(backup.schemaVersion == CURRENT_SCHEMA_VERSION) { "Unsupported backup schema" }
        require(backup.exportedAtEpochMillis >= 0L) { "Invalid export timestamp" }
        require(backup.history.size + backup.templates.size <= MAX_RECORDS) { "Backup is too large" }
        requireUniqueIds(backup.history.map { it.id }, "history")
        requireUniqueIds(backup.templates.map { it.id }, "templates")
        backup.history.forEach { require(validHistory(it)) { "Invalid history record" } }
        backup.templates.forEach { require(validTemplate(it)) { "Invalid template record" } }

        val body = buildString {
            appendLine("$MAGIC\t${backup.schemaVersion}\t${backup.exportedAtEpochMillis}")
            appendLine(
                listOf(
                    "P",
                    backup.preferences.themeMode.name,
                    backup.preferences.largeText.toString(),
                    backup.preferences.reducedMotion.toString(),
                    backup.preferences.autoDeleteHistory.name,
                    backup.preferences.onboardingCompleted.toString(),
                ).joinToString("\t"),
            )
            backup.history.forEach { entry ->
                appendLine(
                    listOf(
                        "H",
                        text(entry.id),
                        entry.createdAtEpochMillis.toString(),
                        text(entry.label),
                        text(entry.currencyCode),
                        text(entry.convertedCurrencyCode),
                        safePlain(entry.subtotal),
                        safePlain(entry.discountAmount),
                        safePlain(entry.taxAmount),
                        safePlain(entry.tipAmount),
                        safePlain(entry.serviceChargeAmount),
                        safePlain(entry.total),
                        safePlain(entry.convertedTotal),
                        safePlain(entry.perPerson),
                        safePlain(entry.convertedPerPerson),
                        entry.splitCount.toString(),
                    ).joinToString("\t"),
                )
            }
            backup.templates.forEach { template ->
                appendLine(
                    listOf(
                        "T",
                        text(template.id),
                        text(template.name),
                        template.createdAtEpochMillis.toString(),
                        safePlain(template.discountPercent),
                        safePlain(template.taxPercent),
                        safePlain(template.tipPercent),
                        safePlain(template.serviceChargePercent),
                        template.splitCount.toString(),
                        text(template.currencyCode),
                        safePlain(template.exchangeRate),
                        text(template.convertedCurrencyCode),
                    ).joinToString("\t"),
                )
            }
        }
        val payload = body + "SHA256\t${sha256(body)}\n"
        require(payload.length <= MAX_BACKUP_CHARS) { "Backup is too large" }
        return payload
    }

    fun decode(payload: String): BackupDecodeResult {
        if (payload.length > MAX_BACKUP_CHARS) return BackupDecodeResult.Failure(BackupDecodeError.TOO_LARGE)
        if (!payload.endsWith('\n')) return BackupDecodeResult.Failure(BackupDecodeError.INVALID_FORMAT)

        val lines = payload.split('\n')
        if (lines.size < 4 || lines.last().isNotEmpty()) {
            return BackupDecodeResult.Failure(BackupDecodeError.INVALID_FORMAT)
        }

        val checksumLine = lines[lines.lastIndex - 1]
        val checksumParts = checksumLine.split('\t')
        if (checksumParts.size != 2 || checksumParts[0] != "SHA256") {
            return BackupDecodeResult.Failure(BackupDecodeError.INVALID_FORMAT)
        }
        val bodyLines = lines.subList(0, lines.lastIndex - 1)
        val body = bodyLines.joinToString(separator = "\n", postfix = "\n")
        if (!MessageDigest.isEqual(
                checksumParts[1].lowercase(Locale.ROOT).toByteArray(StandardCharsets.US_ASCII),
                sha256(body).toByteArray(StandardCharsets.US_ASCII),
            )
        ) {
            return BackupDecodeResult.Failure(BackupDecodeError.CHECKSUM_MISMATCH)
        }

        val header = bodyLines.firstOrNull()?.split('\t')
            ?: return BackupDecodeResult.Failure(BackupDecodeError.INVALID_FORMAT)
        if (header.size != 3 || header[0] != MAGIC) {
            return BackupDecodeResult.Failure(BackupDecodeError.INVALID_FORMAT)
        }
        val version = header[1].toIntOrNull()
            ?: return BackupDecodeResult.Failure(BackupDecodeError.INVALID_FORMAT)
        if (version != CURRENT_SCHEMA_VERSION) {
            return BackupDecodeResult.Failure(BackupDecodeError.UNSUPPORTED_VERSION)
        }
        val exportedAt = header[2].toLongOrNull()
            ?: return BackupDecodeResult.Failure(BackupDecodeError.INVALID_FORMAT)
        if (exportedAt < 0L) return BackupDecodeResult.Failure(BackupDecodeError.INVALID_RECORD)

        var preferences: UserPreferences? = null
        val history = mutableListOf<HistoryRecord>()
        val templates = mutableListOf<CalculationTemplate>()

        for (line in bodyLines.drop(1)) {
            if (line.length > MAX_LINE_CHARS) return BackupDecodeResult.Failure(BackupDecodeError.TOO_LARGE)
            val fields = line.split('\t')
            when (fields.firstOrNull()) {
                "P" -> {
                    if (preferences != null || fields.size != 6) {
                        return BackupDecodeResult.Failure(BackupDecodeError.INVALID_RECORD)
                    }
                    preferences = decodePreferences(fields)
                        ?: return BackupDecodeResult.Failure(BackupDecodeError.INVALID_RECORD)
                }
                "H" -> {
                    if (history.size + templates.size >= MAX_RECORDS || fields.size != 16) {
                        return BackupDecodeResult.Failure(BackupDecodeError.INVALID_RECORD)
                    }
                    history += decodeHistory(fields)
                        ?: return BackupDecodeResult.Failure(BackupDecodeError.INVALID_RECORD)
                }
                "T" -> {
                    if (history.size + templates.size >= MAX_RECORDS || fields.size != 12) {
                        return BackupDecodeResult.Failure(BackupDecodeError.INVALID_RECORD)
                    }
                    templates += decodeTemplate(fields)
                        ?: return BackupDecodeResult.Failure(BackupDecodeError.INVALID_RECORD)
                }
                else -> return BackupDecodeResult.Failure(BackupDecodeError.INVALID_RECORD)
            }
        }

        val restoredPreferences = preferences
            ?: return BackupDecodeResult.Failure(BackupDecodeError.INVALID_FORMAT)
        if (history.map { it.id }.toSet().size != history.size) {
            return BackupDecodeResult.Failure(BackupDecodeError.INVALID_RECORD)
        }
        if (templates.map { it.id }.toSet().size != templates.size) {
            return BackupDecodeResult.Failure(BackupDecodeError.INVALID_RECORD)
        }
        return BackupDecodeResult.Success(
            SpendCalcBackup(
                schemaVersion = version,
                exportedAtEpochMillis = exportedAt,
                history = history,
                templates = templates,
                preferences = restoredPreferences,
            ),
        )
    }

    private fun decodePreferences(fields: List<String>): UserPreferences? = runCatching {
        UserPreferences(
            themeMode = ThemeMode.valueOf(fields[1]),
            largeText = parseBoolean(fields[2]),
            reducedMotion = parseBoolean(fields[3]),
            autoDeleteHistory = AutoDeleteHistory.valueOf(fields[4]),
            onboardingCompleted = parseBoolean(fields[5]),
        )
    }.getOrNull()

    private fun decodeHistory(fields: List<String>): HistoryRecord? = runCatching {
        val record = HistoryRecord(
            id = decodedText(fields[1]),
            createdAtEpochMillis = fields[2].toLong(),
            label = decodedText(fields[3]),
            currencyCode = decodedText(fields[4]).uppercase(Locale.ROOT),
            convertedCurrencyCode = decodedText(fields[5]).uppercase(Locale.ROOT),
            subtotal = decimal(fields[6]),
            discountAmount = decimal(fields[7]),
            taxAmount = decimal(fields[8]),
            tipAmount = decimal(fields[9]),
            serviceChargeAmount = decimal(fields[10]),
            total = decimal(fields[11]),
            convertedTotal = decimal(fields[12]),
            perPerson = decimal(fields[13]),
            convertedPerPerson = decimal(fields[14]),
            splitCount = fields[15].toInt(),
        )
        require(validHistory(record))
        record
    }.getOrNull()

    private fun decodeTemplate(fields: List<String>): CalculationTemplate? = runCatching {
        val template = CalculationTemplate(
            id = decodedText(fields[1]),
            name = decodedText(fields[2]),
            createdAtEpochMillis = fields[3].toLong(),
            discountPercent = decimal(fields[4]),
            taxPercent = decimal(fields[5]),
            tipPercent = decimal(fields[6]),
            serviceChargePercent = decimal(fields[7]),
            splitCount = fields[8].toInt(),
            currencyCode = decodedText(fields[9]).uppercase(Locale.ROOT),
            exchangeRate = decimal(fields[10]),
            convertedCurrencyCode = decodedText(fields[11]).uppercase(Locale.ROOT),
        )
        require(validTemplate(template))
        template
    }.getOrNull()

    private fun validHistory(record: HistoryRecord): Boolean {
        if (record.id.isBlank() || record.id.length > MAX_FIELD_CHARS) return false
        if (record.createdAtEpochMillis < 0L || record.label.length > MAX_FIELD_CHARS) return false
        if (!validCurrency(record.currencyCode) || !validCurrency(record.convertedCurrencyCode)) return false
        if (record.splitCount !in 1..MAX_SPLIT_COUNT) return false
        return listOf(
            record.subtotal,
            record.discountAmount,
            record.taxAmount,
            record.tipAmount,
            record.serviceChargeAmount,
            record.total,
            record.convertedTotal,
            record.perPerson,
            record.convertedPerPerson,
        ).all { value -> value >= BigDecimal.ZERO && validDecimalShape(value) }
    }

    private fun validTemplate(template: CalculationTemplate): Boolean {
        if (template.id.isBlank() || template.id.length > MAX_FIELD_CHARS) return false
        if (template.name.length > MAX_FIELD_CHARS || template.createdAtEpochMillis < 0L) return false
        val errors = calculatorEngine.validate(
            CalculationInput(
                items = emptyList(),
                discountPercent = template.discountPercent,
                taxPercent = template.taxPercent,
                tipPercent = template.tipPercent,
                serviceChargePercent = template.serviceChargePercent,
                splitCount = template.splitCount,
                currencyCode = template.currencyCode,
                exchangeRate = template.exchangeRate,
                convertedCurrencyCode = template.convertedCurrencyCode,
            ),
        )
        return errors.isEmpty()
    }

    private fun parseBoolean(value: String): Boolean = when (value) {
        "true" -> true
        "false" -> false
        else -> error("Invalid boolean")
    }

    private fun decimal(value: String): BigDecimal {
        require(value.length <= MAX_DECIMAL_CHARS)
        require(PLAIN_DECIMAL.matches(value))
        return BigDecimal(value).also { decimal -> require(validDecimalShape(decimal)) }
    }

    private fun safePlain(value: BigDecimal): String {
        require(validDecimalShape(value))
        return value.toPlainString()
    }

    private fun validDecimalShape(value: BigDecimal): Boolean =
        value.scale() in 0..MAX_DECIMAL_SCALE && integerDigits(value) <= MAX_DECIMAL_INTEGER_DIGITS

    private fun integerDigits(value: BigDecimal): Int =
        (value.precision() - value.scale()).coerceAtLeast(1)

    private fun text(value: String): String {
        require(value.length <= MAX_FIELD_CHARS)
        return ENCODER.encodeToString(value.toByteArray(StandardCharsets.UTF_8))
    }

    private fun decodedText(value: String): String {
        require(value.length <= MAX_ENCODED_FIELD_CHARS)
        val decoded = DECODER.decode(value)
        require(decoded.size <= MAX_FIELD_BYTES)
        return String(decoded, StandardCharsets.UTF_8).also { text ->
            require(text.length <= MAX_FIELD_CHARS)
        }
    }

    private fun requireUniqueIds(ids: List<String>, label: String) {
        require(ids.toSet().size == ids.size) { "Duplicate $label identifiers" }
    }

    private fun validCurrency(value: String): Boolean = CURRENCY_CODE.matches(value)

    private fun sha256(value: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(StandardCharsets.UTF_8))
            .joinToString("") { byte -> "%02x".format(byte.toInt() and 0xff) }

    private companion object {
        const val MAGIC = "SPENDCALC_BACKUP"
        const val CURRENT_SCHEMA_VERSION = SpendCalcBackup.CURRENT_SCHEMA_VERSION
        const val MAX_BACKUP_CHARS = 5_000_000
        const val MAX_RECORDS = 10_000
        const val MAX_LINE_CHARS = 16_384
        const val MAX_FIELD_CHARS = 4_096
        const val MAX_FIELD_BYTES = 16_384
        const val MAX_ENCODED_FIELD_CHARS = 24_000
        const val MAX_DECIMAL_CHARS = 128
        const val MAX_DECIMAL_INTEGER_DIGITS = 15
        const val MAX_DECIMAL_SCALE = 12
        const val MAX_SPLIT_COUNT = 1_000_000
        val PLAIN_DECIMAL = Regex("[+-]?\\d+(?:\\.\\d+)?")
        val CURRENCY_CODE = Regex("[A-Z]{3}")
        val ENCODER: Base64.Encoder = Base64.getUrlEncoder().withoutPadding()
        val DECODER: Base64.Decoder = Base64.getUrlDecoder()
    }
}
