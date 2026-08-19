package `in`.sanskar.spendcalc.domain.export

import `in`.sanskar.spendcalc.domain.model.CalculationInput
import `in`.sanskar.spendcalc.domain.model.CalculationResult

interface ExportFormatter {
    val mimeType: String
    val fileExtension: String

    fun format(
        input: CalculationInput,
        result: CalculationResult,
    ): String
}
