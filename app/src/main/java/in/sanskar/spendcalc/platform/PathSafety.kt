package `in`.sanskar.spendcalc.platform

import java.io.File

internal fun File.isWithinDirectory(directory: File): Boolean {
    val root = directory.canonicalFile.toPath()
    val candidate = canonicalFile.toPath()
    return candidate != root && candidate.startsWith(root)
}

internal fun sanitizeExportFileName(value: String): String {
    val sanitized = value
        .replace(Regex("[^A-Za-z0-9._-]"), "_")
        .take(MAX_EXPORT_FILE_NAME_CHARS)

    return if (sanitized.isBlank() || sanitized.all { it == '.' }) {
        DEFAULT_EXPORT_FILE_NAME
    } else {
        sanitized
    }
}

private const val MAX_EXPORT_FILE_NAME_CHARS = 96
private const val DEFAULT_EXPORT_FILE_NAME = "spendcalc-export.txt"
