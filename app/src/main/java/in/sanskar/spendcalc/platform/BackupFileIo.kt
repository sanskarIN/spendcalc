package `in`.sanskar.spendcalc.platform

import android.content.Context
import android.net.Uri
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.CharsetDecoder
import java.nio.charset.CodingErrorAction
import java.nio.charset.StandardCharsets

object BackupFileIo {
    fun write(context: Context, uri: Uri, payload: String) {
        require(payload.length <= MAX_PAYLOAD_CHARS) { "Backup payload is too large" }
        val stream = context.contentResolver.openOutputStream(uri, "wt")
            ?: error("Unable to open backup destination")
        OutputStreamWriter(stream, StandardCharsets.UTF_8).use { writer ->
            writer.write(payload)
        }
    }

    fun read(context: Context, uri: Uri): String {
        val stream = context.contentResolver.openInputStream(uri)
            ?: error("Unable to open backup source")
        return InputStreamReader(stream, strictUtf8Decoder()).use { reader ->
            val result = StringBuilder()
            val buffer = CharArray(BUFFER_SIZE)
            while (true) {
                val count = reader.read(buffer)
                if (count < 0) break
                require(result.length + count <= MAX_PAYLOAD_CHARS) { "Backup payload is too large" }
                result.append(buffer, 0, count)
            }
            result.toString()
        }
    }

    private const val MAX_PAYLOAD_CHARS = 5_000_000
    private const val BUFFER_SIZE = 8_192
}

internal fun strictUtf8Decoder(): CharsetDecoder =
    StandardCharsets.UTF_8.newDecoder()
        .onMalformedInput(CodingErrorAction.REPORT)
        .onUnmappableCharacter(CodingErrorAction.REPORT)
