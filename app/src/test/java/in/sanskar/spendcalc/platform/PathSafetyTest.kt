package `in`.sanskar.spendcalc.platform

import java.io.File
import java.nio.file.Files
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
}
