package `in`.sanskar.spendcalc.platform

import org.junit.Assert.assertEquals
import org.junit.Test

class PdfReceiptExporterTest {
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
