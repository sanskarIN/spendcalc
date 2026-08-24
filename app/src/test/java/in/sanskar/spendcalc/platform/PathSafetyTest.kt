package `in`.sanskar.spendcalc.platform

import java.io.File
import java.nio.ByteBuffer
import java.nio.file.Files
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PathSafetyTest {
    @Test
    fun `accepts files inside the export directory`() {
        val root = Files.createTempDirectory("spendcalc-path-test").toFile()
        try {
            val exports = File(root, "exports").apply { mkdirs() }
            val nested = File(exports, "nested/receipt.pdf").apply {
                parentFile?.mkdirs()
                writeText("test")
            }

            assertTrue(nested.isWithinDirectory(exports))
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun `rejects the export directory itself`() {
        val root = Files.createTempDirectory("spendcalc-path-test").toFile()
        try {
            val exports = File(root, "exports").apply { mkdirs() }

            assertFalse(exports.isWithinDirectory(exports))
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun `rejects sibling directories that share the exports prefix`() {
        val root = Files.createTempDirectory("spendcalc-path-test").toFile()
        try {
            val exports = File(root, "exports").apply { mkdirs() }
            val sibling = File(root, "exports-private/secret.txt").apply {
                parentFile?.mkdirs()
                writeText("secret")
            }

            assertFalse(sibling.isWithinDirectory(exports))
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun `export filenames replace path separators and unsafe characters`() {
        assertEquals(
            ".._private_receipt_1.txt",
            sanitizeExportFileName("../private/receipt 1.txt"),
        )
    }

    @Test
    fun `dot only export filenames fall back to a regular file`() {
        assertEquals("spendcalc-export.txt", sanitizeExportFileName("."))
        assertEquals("spendcalc-export.txt", sanitizeExportFileName(".."))
        assertEquals("spendcalc-export.txt", sanitizeExportFileName("...."))
    }

    @Test
    fun `blank export filenames fall back to a regular file`() {
        assertEquals("spendcalc-export.txt", sanitizeExportFileName(""))
        assertEquals("spendcalc-export.txt", sanitizeExportFileName("   \n\t"))
    }

    @Test
    fun `export filenames are bounded`() {
        assertEquals(96, sanitizeExportFileName("a".repeat(200)).length)
    }

    @Test
    fun `strict backup decoder rejects malformed utf8 bytes`() {
        val malformed = ByteBuffer.wrap(byteArrayOf(0xC3.toByte(), 0x28))

        assertTrue(runCatching { strictUtf8Decoder().decode(malformed) }.isFailure)
    }

    @Test
    fun `pdf ellipsis never leaves a dangling high surrogate`() {
        val input = "a".repeat(76) + "😀" + "tail"

        assertEquals(
            "a".repeat(76) + "…",
            ellipsizePdfLine(input),
        )
    }

    @Test
    fun `pdf ellipsis uses the available ascii line budget`() {
        assertEquals(
            "a".repeat(77) + "…",
            ellipsizePdfLine("a".repeat(90)),
        )
    }

    @Test
    fun `short pdf lines are preserved exactly`() {
        val input = "Dinner 😀 — INR 250.00"

        assertEquals(input, ellipsizePdfLine(input))
    }
}
