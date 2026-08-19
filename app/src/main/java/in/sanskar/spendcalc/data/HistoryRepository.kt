package in.sanskar.spendcalc.data

import in.sanskar.spendcalc.data.local.HistoryDao
import in.sanskar.spendcalc.data.local.HistoryEntity
import in.sanskar.spendcalc.domain.model.CalculationResult
import in.sanskar.spendcalc.domain.model.HistoryRecord
import java.math.BigDecimal
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryRepository(
    private val dao: HistoryDao,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    fun observeHistory(): Flow<List<HistoryRecord>> =
        dao.observeAll().map { entries -> entries.map(HistoryEntity::toDomain) }

    suspend fun save(result: CalculationResult, label: String = "Calculation"): String {
        val id = UUID.randomUUID().toString()
        dao.upsert(
            HistoryEntity(
                id = id,
                createdAtEpochMillis = clock(),
                label = label.trim().ifBlank { "Calculation" },
                currencyCode = result.currencyCode,
                convertedCurrencyCode = result.convertedCurrencyCode,
                subtotal = result.subtotal.toPlainString(),
                discountAmount = result.discountAmount.toPlainString(),
                taxAmount = result.taxAmount.toPlainString(),
                tipAmount = result.tipAmount.toPlainString(),
                serviceChargeAmount = result.serviceChargeAmount.toPlainString(),
                total = result.total.toPlainString(),
                convertedTotal = result.convertedTotal.toPlainString(),
                perPerson = result.perPerson.toPlainString(),
                convertedPerPerson = result.convertedPerPerson.toPlainString(),
                splitCount = result.splitCount,
            ),
        )
        return id
    }

    suspend fun delete(id: String) = dao.deleteById(id)

    suspend fun clear() = dao.clear()

    suspend fun purgeOlderThan(cutoffEpochMillis: Long): Int =
        dao.deleteOlderThan(cutoffEpochMillis)

    private fun HistoryEntity.toDomain(): HistoryRecord =
        HistoryRecord(
            id = id,
            createdAtEpochMillis = createdAtEpochMillis,
            label = label,
            currencyCode = currencyCode,
            convertedCurrencyCode = convertedCurrencyCode,
            subtotal = BigDecimal(subtotal),
            discountAmount = BigDecimal(discountAmount),
            taxAmount = BigDecimal(taxAmount),
            tipAmount = BigDecimal(tipAmount),
            serviceChargeAmount = BigDecimal(serviceChargeAmount),
            total = BigDecimal(total),
            convertedTotal = BigDecimal(convertedTotal),
            perPerson = BigDecimal(perPerson),
            convertedPerPerson = BigDecimal(convertedPerPerson),
            splitCount = splitCount,
        )
}
