package in.sanskar.spendcalc.domain.export

import in.sanskar.spendcalc.domain.model.CalculationInput
import in.sanskar.spendcalc.domain.model.CalculationResult

class ReceiptTextFormatter : ExportFormatter {
    override val mimeType: String = "text/plain"
    override val fileExtension: String = "txt"

    override fun format(input: CalculationInput, result: CalculationResult): String = buildString {
        appendLine("SpendCalc Receipt")
        appendLine("=================")
        input.items.forEachIndexed { index, item ->
            val label = item.name.ifBlank { "Item ${index + 1}" }
            appendLine("$label: ${result.currencyCode} ${item.amount.toPlainString()}")
        }
        appendLine("-----------------")
        appendLine("Subtotal: ${result.currencyCode} ${result.subtotal.toPlainString()}")
        appendLine("Discount: -${result.currencyCode} ${result.discountAmount.toPlainString()}")
        appendLine("Tax: ${result.currencyCode} ${result.taxAmount.toPlainString()}")
        appendLine("Tip: ${result.currencyCode} ${result.tipAmount.toPlainString()}")
        appendLine("Service charge: ${result.currencyCode} ${result.serviceChargeAmount.toPlainString()}")
        appendLine("Total: ${result.currencyCode} ${result.total.toPlainString()}")
        if (result.currencyCode != result.convertedCurrencyCode || input.exchangeRate.compareTo(java.math.BigDecimal.ONE) != 0) {
            appendLine("Converted total: ${result.convertedCurrencyCode} ${result.convertedTotal.toPlainString()}")
        }
        appendLine("Split: ${result.splitCount}")
        appendLine("Per person: ${result.currencyCode} ${result.perPerson.toPlainString()}")
        if (result.currencyCode != result.convertedCurrencyCode || input.exchangeRate.compareTo(java.math.BigDecimal.ONE) != 0) {
            appendLine("Converted per person: ${result.convertedCurrencyCode} ${result.convertedPerPerson.toPlainString()}")
        }
        appendLine()
        appendLine("Made by the Sanskar")
    }
}
