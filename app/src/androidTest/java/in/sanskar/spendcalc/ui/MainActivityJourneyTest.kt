package `in`.sanskar.spendcalc.ui

import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
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

        val amountLabel = composeRule.activity.getString(R.string.item_amount)
        val saveLabel = composeRule.activity.getString(R.string.save_to_history)
        val historyInputLabel = composeRule.activity.getString(R.string.history_label)
        val saveHistoryConfirmLabel = composeRule.activity.getString(R.string.save_history_confirm)
        val historyNavLabel = composeRule.activity.getString(R.string.nav_history)
        val expectedAmount = "INR 25.00"
        val savedHistoryName = "Grocery run"

        composeRule.onNode(
            hasSetTextAction() and hasText(amountLabel, substring = true),
            useUnmergedTree = true,
        ).performTextInput("25.00")

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(expectedAmount).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(expectedAmount).assertExists()

        composeRule.onNodeWithText(saveLabel).performScrollTo().performClick()
        composeRule.onNode(
            hasSetTextAction() and hasText(historyInputLabel, substring = true),
            useUnmergedTree = true,
        ).performTextInput(savedHistoryName)
        composeRule.onNodeWithText(saveHistoryConfirmLabel).performClick()
        composeRule.onNodeWithText(historyNavLabel).performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(expectedAmount).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(savedHistoryName).assertExists()
        composeRule.onNodeWithText(expectedAmount).assertExists()
    }

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
