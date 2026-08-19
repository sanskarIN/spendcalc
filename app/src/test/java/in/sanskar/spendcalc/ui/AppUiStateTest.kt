package `in`.sanskar.spendcalc.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class AppUiStateTest {
    @Test
    fun `repeated identical feedback receives a new sequence`() {
        val first = CalculatorUiState().withFeedback(ActionFeedback.HISTORY_SAVED)
        val second = first.withFeedback(ActionFeedback.HISTORY_SAVED)

        assertEquals(ActionFeedback.HISTORY_SAVED, first.feedback)
        assertEquals(ActionFeedback.HISTORY_SAVED, second.feedback)
        assertNotEquals(first.feedbackSequence, second.feedbackSequence)
        assertEquals(first.feedbackSequence + 1L, second.feedbackSequence)
    }
}
