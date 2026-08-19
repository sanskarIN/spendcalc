package `in`.sanskar.spendcalc.domain.export

import `in`.sanskar.spendcalc.domain.model.CalculationInput
import `in`.sanskar.spendcalc.domain.model.CalculationResult

class CsvExportFormatter : ExportFormatter {
    override val mimeType: String = "text/csv"
    override val fileExtension: String = "csv"

    override fun format(input: CalculationInput, result: CalculationResult): String = buildString {
        appendLine("type,name,value,currency")
        input.items.forEach { item ->
            appendLine(
                listOf(
                    "item",
                    safeTextCell(item.name),
                    item.amount.toPlainString(),
                    result.currencyCode,
                ).joinToString(",", transform = ::csvCell),
            )
        }
        appendSummary("subtotal", result.subtotal.toPlainString(), result.currencyCode)
        appendSummary("discount", result.discountAmount.toPlainString(), result.currencyCode)
        appendSummary("tax", result.taxAmount.toPlainString(), result.currencyCode)
        appendSummary("tip", result.tipAmount.toPlainString(), result.currencyCode)
        appendSummary("service_charge", result.serviceChargeAmount.toPlainString(), result.currencyCode)
        appendSummary("total", result.total.toPlainString(), result.currencyCode)
        appendSummary("converted_total", result.convertedTotal.toPlainString(), result.convertedCurrencyCode)
        appendSummary("per_person", result.perPerson.toPlainString(), result.currencyCode)
        appendSummary("converted_per_person", result.convertedPerPerson.toPlainString(), result.convertedCurrencyCode)
    }

    private fun StringBuilder.appendSummary(name: String, value: String, currency: String) {
        appendLine(listOf("summary", name, value, currency).joinToString(",", transform = ::csvCell))
    }

    private fun safeTextCell(value: String): String {
        val trimmed = value.trim()
        return if (trimmed.startsWithAny('=', '+', '-', '@')) "'$trimmed" else trimmed
    }

    private fun String.startsWithAny(vararg chars: Char): Boolean =
        firstOrNull()?.let { first -> chars.any { it == first } } == true

    private fun csvCell(value: String): String =
        "\"${value.replace("\"", "\"\"")}\""
}
