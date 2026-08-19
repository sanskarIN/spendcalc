package `in`.sanskar.spendcalc.domain

import `in`.sanskar.spendcalc.domain.model.CalculationError
import `in`.sanskar.spendcalc.domain.model.CalculationInput
import `in`.sanskar.spendcalc.domain.model.CalculationOutcome
import `in`.sanskar.spendcalc.domain.model.CalculationResult
import `in`.sanskar.spendcalc.domain.model.RoundingPolicy
import java.math.BigDecimal
import java.util.Locale

/**
 * Precision-safe expense arithmetic.
 *
 * Charge order is intentionally explicit:
 * 1. Sum line items.
 * 2. Apply discount to subtotal.
 * 3. Calculate tax, tip, and service charge from the discounted base.
 * 4. Sum charges and round final monetary values.
 * 5. Apply the manual exchange rate and split count.
 */
class CalculatorEngine(
    private val roundingPolicy: RoundingPolicy = RoundingPolicy(),
) {
    fun calculate(input: CalculationInput): CalculationOutcome {
        val errors = validate(input)
        if (errors.isNotEmpty()) return CalculationOutcome.Failure(errors)

        val subtotalRaw = input.items.fold(BigDecimal.ZERO) { total, item -> total + item.amount }
        val discountRaw = percentageOf(subtotalRaw, input.discountPercent)
        val taxableBaseRaw = subtotalRaw - discountRaw
        val taxRaw = percentageOf(taxableBaseRaw, input.taxPercent)
        val tipRaw = percentageOf(taxableBaseRaw, input.tipPercent)
        val serviceRaw = percentageOf(taxableBaseRaw, input.serviceChargePercent)
        val totalRaw = taxableBaseRaw + taxRaw + tipRaw + serviceRaw
        val convertedTotalRaw = totalRaw.multiply(input.exchangeRate)

        val total = money(totalRaw)
        val convertedTotal = money(convertedTotalRaw)
        val splitDivisor = BigDecimal.valueOf(input.splitCount.toLong())

        return CalculationOutcome.Success(
            CalculationResult(
                subtotal = money(subtotalRaw),
                discountAmount = money(discountRaw),
                taxableBase = money(taxableBaseRaw),
                taxAmount = money(taxRaw),
                tipAmount = money(tipRaw),
                serviceChargeAmount = money(serviceRaw),
                total = total,
                convertedTotal = convertedTotal,
                perPerson = total.divide(
                    splitDivisor,
                    roundingPolicy.moneyScale,
                    roundingPolicy.roundingMode,
                ),
                convertedPerPerson = convertedTotal.divide(
                    splitDivisor,
                    roundingPolicy.moneyScale,
                    roundingPolicy.roundingMode,
                ),
                currencyCode = normalizeCurrencyCode(input.currencyCode),
                convertedCurrencyCode = normalizeCurrencyCode(input.convertedCurrencyCode),
                splitCount = input.splitCount,
            ),
        )
    }

    fun validate(input: CalculationInput): List<CalculationError> = buildList {
        input.items.filter { it.amount < BigDecimal.ZERO }.forEach {
            add(CalculationError.InvalidAmount(it.id))
        }
        if (!validDiscount(input.discountPercent)) add(CalculationError.InvalidDiscount)
        if (!validChargePercentage(input.taxPercent)) add(CalculationError.InvalidTax)
        if (!validChargePercentage(input.tipPercent)) add(CalculationError.InvalidTip)
        if (!validChargePercentage(input.serviceChargePercent)) add(CalculationError.InvalidServiceCharge)
        if (input.splitCount < 1) add(CalculationError.InvalidSplitCount)
        if (!validCurrencyCode(input.currencyCode)) add(CalculationError.InvalidCurrencyCode)
        if (!validCurrencyCode(input.convertedCurrencyCode)) add(CalculationError.InvalidConvertedCurrencyCode)
        if (input.exchangeRate <= BigDecimal.ZERO) add(CalculationError.InvalidExchangeRate)
    }

    private fun percentageOf(base: BigDecimal, percentage: BigDecimal): BigDecimal =
        base.multiply(percentage).divide(
            ONE_HUNDRED,
            roundingPolicy.intermediateScale,
            roundingPolicy.roundingMode,
        )

    private fun money(value: BigDecimal): BigDecimal =
        value.setScale(roundingPolicy.moneyScale, roundingPolicy.roundingMode)

    private fun validDiscount(value: BigDecimal): Boolean =
        value >= BigDecimal.ZERO && value <= ONE_HUNDRED

    private fun validChargePercentage(value: BigDecimal): Boolean =
        value >= BigDecimal.ZERO && value <= MAX_CHARGE_PERCENTAGE

    private fun validCurrencyCode(value: String): Boolean =
        CURRENCY_CODE.matches(normalizeCurrencyCode(value))

    private fun normalizeCurrencyCode(value: String): String =
        value.trim().uppercase(Locale.ROOT)

    private companion object {
        val ONE_HUNDRED = BigDecimal("100")
        val MAX_CHARGE_PERCENTAGE = BigDecimal("1000")
        val CURRENCY_CODE = Regex("[A-Z]{3}")
    }
}
