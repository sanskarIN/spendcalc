package `in`.sanskar.spendcalc.data

import `in`.sanskar.spendcalc.data.local.HistoryDao
import `in`.sanskar.spendcalc.data.local.HistoryEntity
import `in`.sanskar.spendcalc.domain.model.CalculationResult
import `in`.sanskar.spendcalc.domain.model.HistoryRecord
import java.math.BigDecimal
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryRepository(
    private val dao: HistoryDao,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    fun observeHistory(): Flow<List<HistoryRecord>> =
        dao.observeAll().map { entries -> entries.map { it.toDomain() } }

    suspend fun save(result: CalculationResult, label: String = "Calculation"): String {
        val id = UUID.randomUUID().toString()
        dao.upsert(
            HistoryRecord(
                id = id,
                createdAtEpochMillis = clock(),
                label = label.trim().ifBlank { "Calculation" },
                currencyCode = result.currencyCode,
                convertedCurrencyCode = result.convertedCurrencyCode,
                subtotal = result.subtotal,
                discountAmount = result.discountAmount,
                taxAmount = result.taxAmount,
                tipAmount = result.tipAmount,
                serviceChargeAmount = result.serviceChargeAmount,
                total = result.total,
                convertedTotal = result.convertedTotal,
                perPerson = result.perPerson,
                convertedPerPerson = result.convertedPerPerson,
                splitCount = result.splitCount,
            ).toEntity(),
        )
        return id
    }

    suspend fun restore(record: HistoryRecord) {
        dao.upsert(record.toEntity())
    }

    suspend fun delete(id: String) = dao.deleteById(id)

    suspend fun clear() = dao.clear()

    suspend fun purgeOlderThan(cutoffEpochMillis: Long): Int =
        dao.deleteOlderThan(cutoffEpochMillis)

    suspend fun replaceAll(records: List<HistoryRecord>) {
        dao.replaceAll(records.map { it.toEntity() })
    }

    private fun HistoryRecord.toEntity(): HistoryEntity =
        HistoryEntity(
            id = id,
            createdAtEpochMillis = createdAtEpochMillis,
            label = label.trim().ifBlank { "Calculation" },
            currencyCode = currencyCode.trim().uppercase(Locale.ROOT),
            convertedCurrencyCode = convertedCurrencyCode.trim().uppercase(Locale.ROOT),
            subtotal = subtotal.toPlainString(),
            discountAmount = discountAmount.toPlainString(),
            taxAmount = taxAmount.toPlainString(),
            tipAmount = tipAmount.toPlainString(),
            serviceChargeAmount = serviceChargeAmount.toPlainString(),
            total = total.toPlainString(),
            convertedTotal = convertedTotal.toPlainString(),
            perPerson = perPerson.toPlainString(),
            convertedPerPerson = convertedPerPerson.toPlainString(),
            splitCount = splitCount,
        )

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
