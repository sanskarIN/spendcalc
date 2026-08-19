package `in`.sanskar.spendcalc.ui

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
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
        composeRule.setContent {
            SpendCalcTheme(themeMode = ThemeMode.LIGHT, largeText = false) {
                CalculatorScreen(
                    state = CalculatorUiState(),
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
                    onSaveHistory = { _ -> },
                    onSaveTemplate = {},
                    onReset = {},
                    onShareReceipt = {},
                    onShareCsv = {},
                    onSharePdf = {},
                )
            }
        }

        composeRule.onNodeWithText("Expense calculator").assertIsDisplayed()
        composeRule.onNodeWithText("Receipt").assertExists()
    }

    @Test
    fun namedHistoryDialogReturnsTheEnteredLabel() {
        val result = (CalculatorEngine().calculate(CalculationInput(items = emptyList())) as CalculationOutcome.Success).result
        var savedLabel: String? = null

        composeRule.setContent {
            SpendCalcTheme(themeMode = ThemeMode.LIGHT, largeText = false) {
                CalculatorScreen(
                    state = CalculatorUiState(result = result),
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
                    onSaveHistory = { savedLabel = it },
                    onSaveTemplate = {},
                    onReset = {},
                    onShareReceipt = {},
                    onShareCsv = {},
                    onSharePdf = {},
                )
            }
        }

        composeRule.onNodeWithText("Save result").performScrollTo().performClick()
        composeRule.onNodeWithText("Save calculation").assertIsDisplayed()
        composeRule.onNodeWithText("History label (optional)").performTextInput("Grocery run")
        composeRule.onNode(hasText("Save result") and hasAnyAncestor(isDialog())).performClick()

        composeRule.runOnIdle {
            assertEquals("Grocery run", savedLabel)
        }
    }
}
