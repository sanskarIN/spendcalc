package `in`.sanskar.spendcalc.domain.model

import java.math.BigDecimal
import java.math.RoundingMode

/** A single line item in an expense calculation. */
data class ExpenseItem(
    val id: String,
    val name: String,
    val amount: BigDecimal,
)

data class CalculationInput(
    val items: List<ExpenseItem>,
    val discountPercent: BigDecimal = BigDecimal.ZERO,
    val taxPercent: BigDecimal = BigDecimal.ZERO,
    val tipPercent: BigDecimal = BigDecimal.ZERO,
    val serviceChargePercent: BigDecimal = BigDecimal.ZERO,
    val splitCount: Int = 1,
    val currencyCode: String = "INR",
    val exchangeRate: BigDecimal = BigDecimal.ONE,
    val convertedCurrencyCode: String = currencyCode,
)

data class CalculationResult(
    val subtotal: BigDecimal,
    val discountAmount: BigDecimal,
    val taxableBase: BigDecimal,
    val taxAmount: BigDecimal,
    val tipAmount: BigDecimal,
    val serviceChargeAmount: BigDecimal,
    val total: BigDecimal,
    val convertedTotal: BigDecimal,
    val perPerson: BigDecimal,
    val convertedPerPerson: BigDecimal,
    val currencyCode: String,
    val convertedCurrencyCode: String,
    val splitCount: Int,
)

data class RoundingPolicy(
    val moneyScale: Int = 2,
    val intermediateScale: Int = 12,
    val roundingMode: RoundingMode = RoundingMode.HALF_UP,
)

sealed interface CalculationError {
    data class InvalidAmount(val itemId: String) : CalculationError
    data object InvalidDiscount : CalculationError
    data object InvalidTax : CalculationError
    data object InvalidTip : CalculationError
    data object InvalidServiceCharge : CalculationError
    data object InvalidSplitCount : CalculationError
    data object InvalidCurrencyCode : CalculationError
    data object InvalidConvertedCurrencyCode : CalculationError
    data object InvalidExchangeRate : CalculationError
}

sealed interface CalculationOutcome {
    data class Success(val result: CalculationResult) : CalculationOutcome
    data class Failure(val errors: List<CalculationError>) : CalculationOutcome
}
