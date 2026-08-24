package `in`.sanskar.spendcalc.data

import `in`.sanskar.spendcalc.data.local.HistoryDao
import `in`.sanskar.spendcalc.data.local.HistoryEntity
import `in`.sanskar.spendcalc.domain.model.HistoryRecord
import java.math.BigDecimal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class HistoryRestoreRepositoryTest {
    @Test
    fun `replace all removes old records and restores backup records`() = runTest {
        val dao = FakeHistoryDao()
        val repository = HistoryRepository(dao)
        dao.upsert(entity(id = "old", createdAt = 1L, total = "1.00"))

        val restored = listOf(
            record(id = "new-a", createdAt = 20L, total = "20.50"),
            record(id = "new-b", createdAt = 10L, total = "10.25"),
        )

        repository.replaceAll(restored)

        assertEquals(restored, repository.observeHistory().first())
    }

    private fun record(id: String, createdAt: Long, total: String) = HistoryRecord(
        id = id,
        createdAtEpochMillis = createdAt,
        label = id,
        currencyCode = "INR",
        convertedCurrencyCode = "INR",
        subtotal = BigDecimal(total),
        discountAmount = BigDecimal.ZERO,
        taxAmount = BigDecimal.ZERO,
        tipAmount = BigDecimal.ZERO,
        serviceChargeAmount = BigDecimal.ZERO,
        total = BigDecimal(total),
        convertedTotal = BigDecimal(total),
        perPerson = BigDecimal(total),
        convertedPerPerson = BigDecimal(total),
        splitCount = 1,
    )

    private fun entity(id: String, createdAt: Long, total: String) = HistoryEntity(
        id = id,
        createdAtEpochMillis = createdAt,
        label = id,
        currencyCode = "INR",
        convertedCurrencyCode = "INR",
        subtotal = total,
        discountAmount = "0",
        taxAmount = "0",
        tipAmount = "0",
        serviceChargeAmount = "0",
        total = total,
        convertedTotal = total,
        perPerson = total,
        convertedPerPerson = total,
        splitCount = 1,
    )

    private class FakeHistoryDao : HistoryDao {
        private val entries = MutableStateFlow<List<HistoryEntity>>(emptyList())

        override fun observeAll(): Flow<List<HistoryEntity>> = entries

        override suspend fun snapshotAll(): List<HistoryEntity> = entries.value

        override suspend fun upsert(entry: HistoryEntity) {
            entries.value = (entries.value.filterNot { it.id == entry.id } + entry)
                .sortedByDescending { it.createdAtEpochMillis }
        }

        override suspend fun upsertAll(entries: List<HistoryEntity>) {
            val ids = entries.mapTo(mutableSetOf()) { it.id }
            this.entries.value = (this.entries.value.filterNot { it.id in ids } + entries)
                .sortedByDescending { it.createdAtEpochMillis }
        }

        override suspend fun deleteById(id: String) {
            entries.value = entries.value.filterNot { it.id == id }
        }

        override suspend fun clear() {
            entries.value = emptyList()
        }

        override suspend fun deleteOlderThan(cutoffEpochMillis: Long): Int {
            val previous = entries.value
            entries.value = previous.filterNot { it.createdAtEpochMillis < cutoffEpochMillis }
            return previous.size - entries.value.size
        }
    }
}
