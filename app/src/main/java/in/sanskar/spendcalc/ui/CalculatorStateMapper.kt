package `in`.sanskar.spendcalc.ui

import `in`.sanskar.spendcalc.domain.CalculatorEngine
import `in`.sanskar.spendcalc.domain.model.CalculationInput
import `in`.sanskar.spendcalc.domain.model.ExpenseItem

/** Builds an exportable domain input from a currently valid calculator form. */
fun CalculatorUiState.toCalculationInputOrNull(): CalculationInput? {
    if (result == null || issues.isNotEmpty()) return null

    val mappedItems = items.mapIndexed { index, draft ->
        val amount = draft.amount.trim().ifBlank { "0" }.toBigDecimalOrNull() ?: return null
        ExpenseItem(
            id = draft.id,
            name = draft.name.trim().ifBlank { "Item ${index + 1}" },
            amount = amount,
        )
    }

    val input = CalculationInput(
        items = mappedItems,
        discountPercent = discountPercent.trim().ifBlank { "0" }.toBigDecimalOrNull() ?: return null,
        taxPercent = taxPercent.trim().ifBlank { "0" }.toBigDecimalOrNull() ?: return null,
        tipPercent = tipPercent.trim().ifBlank { "0" }.toBigDecimalOrNull() ?: return null,
        serviceChargePercent = serviceChargePercent.trim().ifBlank { "0" }.toBigDecimalOrNull() ?: return null,
        splitCount = splitCount.trim().toIntOrNull() ?: return null,
        currencyCode = currencyCode,
        exchangeRate = exchangeRate.trim().toBigDecimalOrNull() ?: return null,
        convertedCurrencyCode = convertedCurrencyCode,
    )
    return input.takeIf { CalculatorEngine().validate(it).isEmpty() }
}
