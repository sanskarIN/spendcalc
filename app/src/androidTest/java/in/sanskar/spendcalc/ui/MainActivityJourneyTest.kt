package `in`.sanskar.spendcalc.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import `in`.sanskar.spendcalc.MainActivity
import `in`.sanskar.spendcalc.R
import org.junit.Rule
import org.junit.Test

class MainActivityJourneyTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun calculateSaveAndFindHistoryJourney() {
        completeOnboardingIfNeeded()

        val saveLabel = composeRule.activity.getString(R.string.save_to_history)
        val saveHistoryConfirmLabel = composeRule.activity.getString(R.string.save_history_confirm)
        val historyNavLabel = composeRule.activity.getString(R.string.nav_history)
        val expectedAmount = "INR 25.00"
        val savedHistoryName = "Grocery run"

        firstItemAmountField().performTextInput("25.00")

        assertAnyNodeWithText(expectedAmount)

        composeRule.onNodeWithText(saveLabel).performScrollTo().performClick()
        activeDialogTextField().performTextInput(savedHistoryName)
        composeRule.onNodeWithText(saveHistoryConfirmLabel).performClick()
        composeRule.onNodeWithText(historyNavLabel).performClick()

        assertAnyNodeWithText(expectedAmount)
        composeRule.onNodeWithText(savedHistoryName).assertExists()
    }

    private fun assertAnyNodeWithText(text: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodesWithText(text)[0].assertExists()
    }

    private fun firstItemAmountField() = composeRule.onNodeWithTag(
        "calculator-item-amount-0",
        useUnmergedTree = true,
    )

    private fun activeDialogTextField() = composeRule.onNodeWithTag(
        "history-save-label",
        useUnmergedTree = true,
    )

    private fun completeOnboardingIfNeeded() {
        val continueLabel = composeRule.activity.getString(R.string.onboarding_continue)
        composeRule.waitForIdle()
        val onboardingVisible = composeRule
            .onAllNodesWithText(continueLabel)
            .fetchSemanticsNodes()
            .isNotEmpty()
        if (onboardingVisible) {
            composeRule.onNodeWithText(continueLabel).performClick()
            composeRule.waitForIdle()
        }
    }
}
