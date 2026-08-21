package `in`.sanskar.spendcalc.platform

import java.io.File

internal fun File.isWithinDirectory(directory: File): Boolean {
    val root = directory.canonicalFile.toPath()
    val candidate = canonicalFile.toPath()
    return candidate.startsWith(root)
}
