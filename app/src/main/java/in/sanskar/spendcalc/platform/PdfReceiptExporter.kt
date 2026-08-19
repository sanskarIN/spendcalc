package in.sanskar.spendcalc.platform

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import in.sanskar.spendcalc.domain.model.CalculationInput
import in.sanskar.spendcalc.domain.model.CalculationResult
import java.io.File
import java.io.FileOutputStream

class PdfReceiptExporter {
    fun create(
        context: Context,
        input: CalculationInput,
        result: CalculationResult,
    ): File {
        val directory = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(directory, "spendcalc-receipt-${System.currentTimeMillis()}.pdf")
        val document = PdfDocument()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 13f
            color = android.graphics.Color.BLACK
        }

        var pageNumber = 0
        var page: PdfDocument.Page? = null
        var y = TOP_MARGIN

        fun startPage() {
            page?.let(document::finishPage)
            pageNumber += 1
            page = document.startPage(
                PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create(),
            )
            y = TOP_MARGIN
        }

        fun line(text: String, bold: Boolean = false, spacingAfter: Float = LINE_HEIGHT) {
            if (page == null || y > PAGE_HEIGHT - BOTTOM_MARGIN) startPage()
            paint.typeface = if (bold) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.DEFAULT
            page!!.canvas.drawText(text.ellipsizeForPdf(), LEFT_MARGIN, y, paint)
            y += spacingAfter
        }

        try {
            startPage()
            paint.textSize = 20f
            line("SpendCalc Receipt", bold = true, spacingAfter = 28f)
            paint.textSize = 13f
            line("Made by the Sanskar", spacingAfter = 24f)

            input.items.forEachIndexed { index, item ->
                val name = item.name.ifBlank { "Item ${index + 1}" }
                line("$name  |  ${result.currencyCode} ${item.amount.toPlainString()}")
            }

            line("", spacingAfter = 12f)
            line("Subtotal: ${result.currencyCode} ${result.subtotal.toPlainString()}")
            line("Discount: -${result.currencyCode} ${result.discountAmount.toPlainString()}")
            line("Tax: ${result.currencyCode} ${result.taxAmount.toPlainString()}")
            line("Tip: ${result.currencyCode} ${result.tipAmount.toPlainString()}")
            line("Service charge: ${result.currencyCode} ${result.serviceChargeAmount.toPlainString()}")
            line("Total: ${result.currencyCode} ${result.total.toPlainString()}", bold = true)
            if (result.currencyCode != result.convertedCurrencyCode || result.convertedTotal != result.total) {
                line(
                    "Converted total: ${result.convertedCurrencyCode} ${result.convertedTotal.toPlainString()}",
                    bold = true,
                )
            }
            line("Split: ${result.splitCount}")
            line("Per person: ${result.currencyCode} ${result.perPerson.toPlainString()}")
            if (result.currencyCode != result.convertedCurrencyCode || result.convertedPerPerson != result.perPerson) {
                line(
                    "Converted per person: ${result.convertedCurrencyCode} ${result.convertedPerPerson.toPlainString()}",
                )
            }

            page?.let(document::finishPage)
            page = null
            FileOutputStream(file).use(document::writeTo)
        } finally {
            page?.let { unfinishedPage ->
                runCatching { document.finishPage(unfinishedPage) }
            }
            document.close()
        }
        return file
    }

    private fun String.ellipsizeForPdf(maxCharacters: Int = 78): String =
        if (length <= maxCharacters) this else take(maxCharacters - 1) + "…"

    private companion object {
        const val PAGE_WIDTH = 595
        const val PAGE_HEIGHT = 842
        const val LEFT_MARGIN = 42f
        const val TOP_MARGIN = 52f
        const val BOTTOM_MARGIN = 48f
        const val LINE_HEIGHT = 20f
    }
}
