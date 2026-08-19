package `in`.sanskar.spendcalc.ui

import `in`.sanskar.spendcalc.domain.model.CalculationResult
import java.util.UUID

data class ExpenseItemDraft(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val amount: String = "",
)

enum class FormIssue {
    ITEM_AMOUNT,
    DISCOUNT,
    TAX,
    TIP,
    SERVICE_CHARGE,
    SPLIT_COUNT,
    CURRENCY,
    CONVERTED_CURRENCY,
    EXCHANGE_RATE,
}

enum class ActionFeedback {
    NONE,
    HISTORY_SAVED,
    TEMPLATE_SAVED,
    DELETED,
    HISTORY_CLEARED,
    BACKUP_EXPORTED,
    BACKUP_RESTORED,
    BACKUP_FAILED,
}

data class CalculatorUiState(
    val items: List<ExpenseItemDraft> = listOf(ExpenseItemDraft()),
    val discountPercent: String = "0",
    val taxPercent: String = "0",
    val tipPercent: String = "0",
    val serviceChargePercent: String = "0",
    val splitCount: String = "1",
    val currencyCode: String = "INR",
    val exchangeRate: String = "1",
    val convertedCurrencyCode: String = "INR",
    val result: CalculationResult? = null,
    val issues: Set<FormIssue> = emptySet(),
    val feedback: ActionFeedback = ActionFeedback.NONE,
)
