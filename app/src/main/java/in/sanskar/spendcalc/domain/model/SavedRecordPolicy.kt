package `in`.sanskar.spendcalc.domain.model

import java.math.BigDecimal

const val MAX_SAVED_ID_CHARS = 128
const val MAX_SAVED_RESULT_INTEGER_DIGITS = 34
const val MAX_SAVED_RESULT_SCALE = 12
const val MAX_SAVED_SPLIT_COUNT = 1_000_000

private val SAVED_CURRENCY_CODE = Regex("[A-Z]{3}")

fun isValidSavedId(value: String): Boolean =
    value.isNotBlank() &&
        value.length <= MAX_SAVED_ID_CHARS &&
        isWellFormedUtf16(value)

fun requireValidSavedId(value: String): String {
    require(isValidSavedId(value)) { "Invalid saved record identifier" }
    return value
}

fun hasUniqueSavedIds(ids: List<String>): Boolean =
    ids.toSet().size == ids.size

fun requireUniqueSavedIds(ids: List<String>, recordType: String) {
    require(hasUniqueSavedIds(ids)) { "Duplicate $recordType identifiers" }
}

fun isCanonicalSavedCurrencyCode(value: String): Boolean =
    SAVED_CURRENCY_CODE.matches(value)

fun isValidSavedResultDecimal(value: BigDecimal): Boolean =
    value >= BigDecimal.ZERO &&
        value.scale() in 0..MAX_SAVED_RESULT_SCALE &&
        integerDigits(value) <= MAX_SAVED_RESULT_INTEGER_DIGITS

fun isValidHistoryRecord(record: HistoryRecord): Boolean =
    isValidSavedId(record.id) &&
        record.createdAtEpochMillis >= 0L &&
        isValidSavedName(record.label) &&
        isCanonicalSavedCurrencyCode(record.currencyCode) &&
        isCanonicalSavedCurrencyCode(record.convertedCurrencyCode) &&
        record.splitCount in 1..MAX_SAVED_SPLIT_COUNT &&
        listOf(
            record.subtotal,
            record.discountAmount,
            record.taxAmount,
            record.tipAmount,
            record.serviceChargeAmount,
            record.total,
            record.convertedTotal,
            record.perPerson,
            record.convertedPerPerson,
        ).all(::isValidSavedResultDecimal)

fun requireValidHistoryRecord(record: HistoryRecord): HistoryRecord {
    require(isValidHistoryRecord(record)) { "Invalid history record" }
    return record
}

fun isValidTemplateEnvelope(template: CalculationTemplate): Boolean =
    isValidSavedId(template.id) &&
        template.createdAtEpochMillis >= 0L &&
        isValidSavedName(template.name) &&
        isCanonicalSavedCurrencyCode(template.currencyCode) &&
        isCanonicalSavedCurrencyCode(template.convertedCurrencyCode)

fun requireValidTemplateEnvelope(template: CalculationTemplate): CalculationTemplate {
    require(isValidTemplateEnvelope(template)) { "Invalid template record envelope" }
    return template
}

private fun integerDigits(value: BigDecimal): Int =
    (value.precision() - value.scale()).coerceAtLeast(1)
