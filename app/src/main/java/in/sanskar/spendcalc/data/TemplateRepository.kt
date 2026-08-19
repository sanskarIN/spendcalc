package in.sanskar.spendcalc.data

import in.sanskar.spendcalc.data.local.TemplateDao
import in.sanskar.spendcalc.data.local.TemplateEntity
import in.sanskar.spendcalc.domain.model.CalculationInput
import in.sanskar.spendcalc.domain.model.CalculationTemplate
import java.math.BigDecimal
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TemplateRepository(
    private val dao: TemplateDao,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    fun observeTemplates(): Flow<List<CalculationTemplate>> =
        dao.observeAll().map { templates -> templates.map { it.toDomain() } }

    suspend fun save(name: String, input: CalculationInput): String {
        val id = UUID.randomUUID().toString()
        dao.upsert(
            TemplateEntity(
                id = id,
                name = name.trim().ifBlank { "Template" },
                createdAtEpochMillis = clock(),
                discountPercent = input.discountPercent.toPlainString(),
                taxPercent = input.taxPercent.toPlainString(),
                tipPercent = input.tipPercent.toPlainString(),
                serviceChargePercent = input.serviceChargePercent.toPlainString(),
                splitCount = input.splitCount,
                currencyCode = input.currencyCode.trim().uppercase(),
                exchangeRate = input.exchangeRate.toPlainString(),
                convertedCurrencyCode = input.convertedCurrencyCode.trim().uppercase(),
            ),
        )
        return id
    }

    suspend fun delete(id: String) = dao.deleteById(id)

    private fun TemplateEntity.toDomain(): CalculationTemplate =
        CalculationTemplate(
            id = id,
            name = name,
            createdAtEpochMillis = createdAtEpochMillis,
            discountPercent = BigDecimal(discountPercent),
            taxPercent = BigDecimal(taxPercent),
            tipPercent = BigDecimal(tipPercent),
            serviceChargePercent = BigDecimal(serviceChargePercent),
            splitCount = splitCount,
            currencyCode = currencyCode,
            exchangeRate = BigDecimal(exchangeRate),
            convertedCurrencyCode = convertedCurrencyCode,
        )
}
