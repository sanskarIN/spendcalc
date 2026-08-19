package `in`.sanskar.spendcalc.data

import `in`.sanskar.spendcalc.data.local.HistoryDao
import `in`.sanskar.spendcalc.data.local.HistoryEntity
import `in`.sanskar.spendcalc.domain.model.CalculationResult
import `in`.sanskar.spendcalc.domain.model.HistoryRecord
import `in`.sanskar.spendcalc.domain.model.normalizeSavedName
import `in`.sanskar.spendcalc.domain.model.requireUniqueSavedIds
import `in`.sanskar.spendcalc.domain.model.requireValidHistoryRecord
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

    suspend fun snapshot(): List<HistoryRecord> =
        dao.snapshotAll().map { it.toDomain() }

    suspend fun save(result: CalculationResult, label: String = "Calculation"): String {
        val id = UUID.randomUUID().toString()
        dao.upsert(
            HistoryRecord(
                id = id,
                createdAtEpochMillis = clock(),
                label = normalizeSavedName(label, DEFAULT_HISTORY_LABEL),
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
        requireUniqueSavedIds(records.map { it.id }, "history")
        val entities = records.map { it.toEntity() }
        dao.replaceAll(entities)
    }

    private fun HistoryRecord.toEntity(): HistoryEntity {
        val normalized = copy(
            currencyCode = currencyCode.trim().uppercase(Locale.ROOT),
            convertedCurrencyCode = convertedCurrencyCode.trim().uppercase(Locale.ROOT),
        )
        requireValidHistoryRecord(normalized)
        return HistoryEntity(
            id = normalized.id,
            createdAtEpochMillis = normalized.createdAtEpochMillis,
            label = normalized.label,
            currencyCode = normalized.currencyCode,
            convertedCurrencyCode = normalized.convertedCurrencyCode,
            subtotal = normalized.subtotal.toPlainString(),
            discountAmount = normalized.discountAmount.toPlainString(),
            taxAmount = normalized.taxAmount.toPlainString(),
            tipAmount = normalized.tipAmount.toPlainString(),
            serviceChargeAmount = normalized.serviceChargeAmount.toPlainString(),
            total = normalized.total.toPlainString(),
            convertedTotal = normalized.convertedTotal.toPlainString(),
            perPerson = normalized.perPerson.toPlainString(),
            convertedPerPerson = normalized.convertedPerPerson.toPlainString(),
            splitCount = normalized.splitCount,
        )
    }

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

    private companion object {
        const val DEFAULT_HISTORY_LABEL = "Calculation"
    }
}
