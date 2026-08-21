package `in`.sanskar.spendcalc.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import `in`.sanskar.spendcalc.domain.model.HistoryRecord
import `in`.sanskar.spendcalc.domain.model.ThemeMode
import `in`.sanskar.spendcalc.ui.screens.HistoryScreen
import `in`.sanskar.spendcalc.ui.theme.SpendCalcTheme
import java.math.BigDecimal
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HistoryScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun historySearchFiltersBySavedLabel() {
        composeRule.setContent {
            SpendCalcTheme(themeMode = ThemeMode.LIGHT, largeText = false) {
                HistoryScreen(
                    history = listOf(
                        historyRecord(id = "grocery", label = "Grocery run", amount = "25.00"),
                        historyRecord(id = "taxi", label = "Taxi", amount = "12.50"),
                    ),
                    onDelete = {},
                    onClear = {},
                )
            }
        }

        composeRule.onNode(
            hasSetTextAction() and hasText("Search history", substring = true),
            useUnmergedTree = true,
        ).performTextInput("Grocery")

        composeRule.onNodeWithText("Grocery run").assertIsDisplayed()
        composeRule.onNodeWithText("Taxi").assertDoesNotExist()
    }

    private fun historyRecord(
        id: String,
        label: String,
        amount: String,
    ): HistoryRecord {
        val value = BigDecimal(amount)
        return HistoryRecord(
            id = id,
            createdAtEpochMillis = 1_700_000_000_000L,
            label = label,
            currencyCode = "INR",
            convertedCurrencyCode = "INR",
            subtotal = value,
            discountAmount = BigDecimal.ZERO,
            taxAmount = BigDecimal.ZERO,
            tipAmount = BigDecimal.ZERO,
            serviceChargeAmount = BigDecimal.ZERO,
            total = value,
            convertedTotal = value,
            perPerson = value,
            convertedPerPerson = value,
            splitCount = 1,
        )
    }
}
