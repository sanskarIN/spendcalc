package `in`.sanskar.spendcalc.data

import `in`.sanskar.spendcalc.data.local.TemplateDao
import `in`.sanskar.spendcalc.data.local.TemplateEntity
import `in`.sanskar.spendcalc.domain.CalculatorEngine
import `in`.sanskar.spendcalc.domain.model.CalculationInput
import `in`.sanskar.spendcalc.domain.model.CalculationTemplate
import `in`.sanskar.spendcalc.domain.model.normalizeSavedName
import `in`.sanskar.spendcalc.domain.model.requireUniqueSavedIds
import `in`.sanskar.spendcalc.domain.model.requireValidTemplateEnvelope
import java.math.BigDecimal
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TemplateRepository(
    private val dao: TemplateDao,
    private val clock: () -> Long = System::currentTimeMillis,
    private val calculatorEngine: CalculatorEngine = CalculatorEngine(),
) {
    fun observeTemplates(): Flow<List<CalculationTemplate>> =
        dao.observeAll().map { templates -> templates.map { it.toDomain() } }

    suspend fun snapshot(): List<CalculationTemplate> =
        dao.snapshotAll().map { it.toDomain() }

    suspend fun save(name: String, input: CalculationInput): String {
        requireValidTemplateSettings(input.copy(items = emptyList()))
        val id = UUID.randomUUID().toString()
        dao.upsert(
            CalculationTemplate(
                id = id,
                name = normalizeSavedName(name, DEFAULT_TEMPLATE_NAME),
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
        requireUniqueSavedIds(templates.map { it.id }, "template")
        val entities = templates.map { it.toEntity() }
        dao.replaceAll(entities)
    }

    private fun CalculationTemplate.toEntity(): TemplateEntity {
        val normalized = copy(
            currencyCode = currencyCode.trim().uppercase(Locale.ROOT),
            convertedCurrencyCode = convertedCurrencyCode.trim().uppercase(Locale.ROOT),
        )
        requireValidTemplateEnvelope(normalized)
        requireValidTemplateSettings(
            CalculationInput(
                items = emptyList(),
                discountPercent = normalized.discountPercent,
                taxPercent = normalized.taxPercent,
                tipPercent = normalized.tipPercent,
                serviceChargePercent = normalized.serviceChargePercent,
                splitCount = normalized.splitCount,
                currencyCode = normalized.currencyCode,
                exchangeRate = normalized.exchangeRate,
                convertedCurrencyCode = normalized.convertedCurrencyCode,
            ),
        )
        return TemplateEntity(
            id = normalized.id,
            name = normalized.name,
            createdAtEpochMillis = normalized.createdAtEpochMillis,
            discountPercent = normalized.discountPercent.toPlainString(),
            taxPercent = normalized.taxPercent.toPlainString(),
            tipPercent = normalized.tipPercent.toPlainString(),
            serviceChargePercent = normalized.serviceChargePercent.toPlainString(),
            splitCount = normalized.splitCount,
            currencyCode = normalized.currencyCode,
            exchangeRate = normalized.exchangeRate.toPlainString(),
            convertedCurrencyCode = normalized.convertedCurrencyCode,
        )
    }

    private fun requireValidTemplateSettings(input: CalculationInput) {
        val errors = calculatorEngine.validate(input)
        require(errors.isEmpty()) { "Invalid template settings: $errors" }
    }

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

    private companion object {
        const val DEFAULT_TEMPLATE_NAME = "Template"
    }
}
