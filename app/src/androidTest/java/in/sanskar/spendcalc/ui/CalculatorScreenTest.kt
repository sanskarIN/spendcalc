package `in`.sanskar.spendcalc.ui

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNode
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import `in`.sanskar.spendcalc.domain.CalculatorEngine
import `in`.sanskar.spendcalc.domain.model.CalculationInput
import `in`.sanskar.spendcalc.domain.model.CalculationOutcome
import `in`.sanskar.spendcalc.domain.model.CalculationResult
import `in`.sanskar.spendcalc.domain.model.MAX_SAVED_NAME_CHARS
import `in`.sanskar.spendcalc.domain.model.ThemeMode
import `in`.sanskar.spendcalc.ui.screens.CalculatorScreen
import `in`.sanskar.spendcalc.ui.theme.SpendCalcTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CalculatorScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun calculatorFormAndReceiptAreInTheComposition() {
        setCalculatorContent(CalculatorUiState())

        composeRule.onNodeWithText("Expense calculator").assertIsDisplayed()
        composeRule.onNodeWithText("Receipt").assertExists()
    }

    @Test
    fun namedHistoryDialogReturnsTheEnteredLabel() {
        var savedLabel: String? = null
        setCalculatorContent(
            state = CalculatorUiState(result = zeroResult()),
            onSaveHistory = { savedLabel = it },
        )

        openHistoryDialog()
        historyLabelField().performTextInput("Grocery run")
        composeRule.onNodeWithText("Save").performClick()

        composeRule.runOnIdle {
            assertEquals("Grocery run", savedLabel)
        }
    }

    @Test
    fun namedHistoryDialogDoesNotSplitEmojiAtBoundary() {
        var savedLabel: String? = null
        val prefix = "x".repeat(MAX_SAVED_NAME_CHARS - 1)
        setCalculatorContent(
            state = CalculatorUiState(result = zeroResult()),
            onSaveHistory = { savedLabel = it },
        )

        openHistoryDialog()
        historyLabelField().performTextInput(prefix + "😀")
        composeRule.onNodeWithText("Save").performClick()

        composeRule.runOnIdle {
            assertEquals(prefix, savedLabel)
        }
    }

    @Test
    fun templateDialogReturnsNameAndShowsLengthGuidance() {
        var savedTemplate: String? = null
        setCalculatorContent(
            state = CalculatorUiState(result = zeroResult()),
            onSaveTemplate = { savedTemplate = it },
        )

        openTemplateDialog()
        composeRule.onNodeWithText("Give this template a short name. Up to 120 characters.").assertIsDisplayed()
        templateNameField().performTextInput("Dinner preset")
        composeRule.onNodeWithText("Save").performClick()

        composeRule.runOnIdle {
            assertEquals("Dinner preset", savedTemplate)
        }
    }

    @Test
    fun templateDialogDoesNotSplitEmojiAtBoundary() {
        var savedTemplate: String? = null
        val prefix = "t".repeat(MAX_SAVED_NAME_CHARS - 1)
        setCalculatorContent(
            state = CalculatorUiState(result = zeroResult()),
            onSaveTemplate = { savedTemplate = it },
        )

        openTemplateDialog()
        templateNameField().performTextInput(prefix + "😀")
        composeRule.onNodeWithText("Save").performClick()

        composeRule.runOnIdle {
            assertEquals(prefix, savedTemplate)
        }
    }

    private fun openHistoryDialog() {
        composeRule.onNodeWithText("Save result").performScrollTo().performClick()
        composeRule.onNodeWithText("Save calculation").assertIsDisplayed()
    }

    private fun openTemplateDialog() {
        composeRule.onNodeWithText("Save template").performScrollTo().performClick()
        composeRule.onNodeWithText("Template name").assertIsDisplayed()
    }

    private fun historyLabelField() = composeRule.onNode(
        hasSetTextAction() and hasText("History label (optional)", substring = true),
        useUnmergedTree = true,
    )

    private fun templateNameField() = composeRule.onNode(
        hasSetTextAction() and hasText("Template name", substring = true),
        useUnmergedTree = true,
    )

    private fun zeroResult(): CalculationResult {
        val outcome = CalculatorEngine().calculate(CalculationInput(items = emptyList()))
        return (outcome as CalculationOutcome.Success).result
    }

    private fun setCalculatorContent(
        state: CalculatorUiState,
        onSaveHistory: (String) -> Unit = {},
        onSaveTemplate: (String) -> Unit = {},
    ) {
        composeRule.setContent {
            SpendCalcTheme(themeMode = ThemeMode.LIGHT, largeText = false) {
                CalculatorScreen(
                    state = state,
                    onItemNameChange = { _, _ -> },
                    onItemAmountChange = { _, _ -> },
                    onAddItem = {},
                    onRemoveItem = {},
                    onDiscountChange = {},
                    onTaxChange = {},
                    onTipChange = {},
                    onServiceChargeChange = {},
                    onSplitCountChange = {},
                    onCurrencyChange = {},
                    onExchangeRateChange = {},
                    onConvertedCurrencyChange = {},
                    onSaveHistory = onSaveHistory,
                    onSaveTemplate = onSaveTemplate,
                    onReset = {},
                    onShareReceipt = {},
                    onShareCsv = {},
                    onSharePdf = {},
                )
            }
        }
    }
}
