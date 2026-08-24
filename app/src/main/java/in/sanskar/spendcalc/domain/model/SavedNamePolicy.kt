package `in`.sanskar.spendcalc.domain.model

fun truncateUtf16Safely(value: String, maxChars: Int): String {
    require(maxChars >= 0) { "maxChars must not be negative" }
    if (maxChars == 0) return ""

    val endIndex = if (value.length <= maxChars) {
        value.length
    } else if (
        Character.isHighSurrogate(value[maxChars - 1]) &&
        Character.isLowSurrogate(value[maxChars])
    ) {
        maxChars - 1
    } else {
        maxChars
    }

    val bounded = value.substring(0, endIndex)
    return if (bounded.lastOrNull()?.let(Character::isHighSurrogate) == true) {
        bounded.dropLast(1)
    } else {
        bounded
    }
}

fun normalizeSavedName(value: String, fallback: String): String {
    require(isWellFormedUtf16(fallback)) { "Fallback saved name must be valid Unicode" }
    require(fallback.isNotBlank()) { "Fallback saved name must not be blank" }
    require(fallback.length <= MAX_SAVED_NAME_CHARS) { "Fallback saved name is too long" }

    val trimmed = value.trim()
    val bounded = truncateUtf16Safely(trimmed, MAX_SAVED_NAME_CHARS)
    require(isWellFormedUtf16(bounded)) { "Saved name must be valid Unicode" }
    return bounded.ifBlank { fallback }
}

fun requireValidSavedName(value: String): String {
    require(value.isNotBlank()) { "Saved name must not be blank" }
    require(value.length <= MAX_SAVED_NAME_CHARS) { "Saved name is too long" }
    require(isWellFormedUtf16(value)) { "Saved name must be valid Unicode" }
    return value
}

fun isValidSavedName(value: String): Boolean =
    value.isNotBlank() &&
        value.length <= MAX_SAVED_NAME_CHARS &&
        isWellFormedUtf16(value)

fun isWellFormedUtf16(value: String): Boolean {
    var index = 0
    while (index < value.length) {
        val current = value[index]
        when {
            Character.isHighSurrogate(current) -> {
                if (index + 1 >= value.length || !Character.isLowSurrogate(value[index + 1])) {
                    return false
                }
                index += 2
            }
            Character.isLowSurrogate(current) -> return false
            else -> index += 1
        }
    }
    return true
}
