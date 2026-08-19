package `in`.sanskar.spendcalc.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SavedNamePolicyTest {
    @Test
    fun `truncation never splits a surrogate pair`() {
        val value = "x".repeat(MAX_SAVED_NAME_CHARS - 1) + "😀" + "tail"

        val truncated = truncateUtf16Safely(value, MAX_SAVED_NAME_CHARS)

        assertEquals("x".repeat(MAX_SAVED_NAME_CHARS - 1), truncated)
        assertTrue(isWellFormedUtf16(truncated))
    }

    @Test
    fun `normalization trims caps and preserves valid Unicode`() {
        val value = "  ${"a".repeat(MAX_SAVED_NAME_CHARS - 2)}😀tail  "

        val normalized = normalizeSavedName(value, "Fallback")

        assertTrue(normalized.length <= MAX_SAVED_NAME_CHARS)
        assertTrue(isWellFormedUtf16(normalized))
        assertTrue(normalized.startsWith("a"))
    }

    @Test
    fun `normalization uses fallback for blank input`() {
        assertEquals("Calculation", normalizeSavedName("   ", "Calculation"))
    }

    @Test
    fun `validation preserves surrounding whitespace when content is nonblank`() {
        val value = "  Grocery run  "

        assertEquals(value, requireValidSavedName(value))
        assertTrue(isValidSavedName(value))
    }

    @Test
    fun `validation rejects malformed UTF-16`() {
        val malformed = "broken\uD83D"

        assertFalse(isWellFormedUtf16(malformed))
        assertFalse(isValidSavedName(malformed))
        assertTrue(runCatching { requireValidSavedName(malformed) }.isFailure)
    }
}
