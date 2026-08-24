package `in`.sanskar.spendcalc.platform

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object ExportManager {
    fun shareText(
        context: Context,
        title: String,
        text: String,
        mimeType: String = "text/plain",
    ) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, title))
    }

    fun createTextFile(
        context: Context,
        fileName: String,
        text: String,
    ): File {
        val directory = File(context.cacheDir, "exports").apply { mkdirs() }
        return File(directory, safeFileName(fileName)).also { file ->
            file.writeText(text, Charsets.UTF_8)
        }
    }

    fun shareTextFile(
        context: Context,
        chooserTitle: String,
        fileName: String,
        mimeType: String,
        text: String,
    ) {
        shareFile(
            context = context,
            chooserTitle = chooserTitle,
            file = createTextFile(context, fileName, text),
            mimeType = mimeType,
        )
    }

    fun shareFile(
        context: Context,
        chooserTitle: String,
        file: File,
        mimeType: String,
    ) {
        val exportDirectory = File(context.cacheDir, "exports")
        require(file.isWithinDirectory(exportDirectory)) {
            "Only SpendCalc cache exports may be shared"
        }
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.files",
            file,
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, chooserTitle))
    }

    private fun safeFileName(value: String): String =
        value.replace(Regex("[^A-Za-z0-9._-]"), "_").take(96).ifBlank { "spendcalc-export.txt" }
}
