package in.sanskar.spendcalc.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import in.sanskar.spendcalc.domain.model.ThemeMode
import in.sanskar.spendcalc.ui.screens.CalculatorScreen
import in.sanskar.spendcalc.ui.theme.SpendCalcTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CalculatorScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun calculatorFormAndReceiptAreVisible() {
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
                    onSaveHistory = {},
                    onSaveTemplate = {},
                    onReset = {},
                    onShareReceipt = {},
                    onShareCsv = {},
                    onSharePdf = {},
                )
            }
        }

        composeRule.onNodeWithText("Expense calculator").assertIsDisplayed()
        composeRule.onNodeWithText("Receipt").assertIsDisplayed()
    }
}
