package `in`.sanskar.spendcalc.data

import `in`.sanskar.spendcalc.data.local.TemplateDao
import `in`.sanskar.spendcalc.data.local.TemplateEntity
import `in`.sanskar.spendcalc.domain.model.CalculationInput
import `in`.sanskar.spendcalc.domain.model.CalculationTemplate
import java.math.BigDecimal
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TemplateRepository(
    private val dao: TemplateDao,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    fun observeTemplates(): Flow<List<CalculationTemplate>> =
        dao.observeAll().map { templates -> templates.map { it.toDomain() } }

    suspend fun snapshot(): List<CalculationTemplate> =
        dao.snapshotAll().map { it.toDomain() }

    suspend fun save(name: String, input: CalculationInput): String {
        val id = UUID.randomUUID().toString()
        dao.upsert(
            CalculationTemplate(
                id = id,
                name = name.trim().ifBlank { "Template" },
                createdAtEpochMillis = clock(),
                discountPercent = input.discountPercent,
                taxPercent = input.taxPercent,
                tipPercent = input.tipPercent,
                serviceChargePercent = input.serviceChargePercent,
                splitCount = input.splitCount,
                currencyCode = input.currencyCode.trim().uppercase(Locale.ROOT),
                exchangeRate = input.exchangeRate,
                convertedCurrencyCode = input.convertedCurrencyCode.trim().uppercase(Locale.ROOT),
            ).toEntity(),
        )
        return id
    }

    suspend fun restore(template: CalculationTemplate) {
        dao.upsert(template.toEntity())
    }

    suspend fun delete(id: String) = dao.deleteById(id)

    suspend fun replaceAll(templates: List<CalculationTemplate>) {
        dao.replaceAll(templates.map { it.toEntity() })
    }

    private fun CalculationTemplate.toEntity(): TemplateEntity =
        TemplateEntity(
            id = id,
            name = name.trim().ifBlank { "Template" },
            createdAtEpochMillis = createdAtEpochMillis,
            discountPercent = discountPercent.toPlainString(),
            taxPercent = taxPercent.toPlainString(),
            tipPercent = tipPercent.toPlainString(),
            serviceChargePercent = serviceChargePercent.toPlainString(),
            splitCount = splitCount,
            currencyCode = currencyCode.trim().uppercase(Locale.ROOT),
            exchangeRate = exchangeRate.toPlainString(),
            convertedCurrencyCode = convertedCurrencyCode.trim().uppercase(Locale.ROOT),
        )

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
