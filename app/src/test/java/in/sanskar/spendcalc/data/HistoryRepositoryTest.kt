package `in`.sanskar.spendcalc.data

import `in`.sanskar.spendcalc.data.local.HistoryDao
import `in`.sanskar.spendcalc.data.local.HistoryEntity
import `in`.sanskar.spendcalc.domain.CalculatorEngine
import `in`.sanskar.spendcalc.domain.model.CalculationInput
import `in`.sanskar.spendcalc.domain.model.CalculationOutcome
import `in`.sanskar.spendcalc.domain.model.ExpenseItem
import `in`.sanskar.spendcalc.domain.model.HistoryRecord
import `in`.sanskar.spendcalc.domain.model.MAX_SAVED_NAME_CHARS
import java.math.BigDecimal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HistoryRepositoryTest {
    @Test
    fun `saves precision-safe result and maps it back`() = runTest {
        val dao = FakeHistoryDao()
        val repository = HistoryRepository(dao = dao, clock = { 1234L })
        val input = CalculationInput(
            items = listOf(ExpenseItem("1", "Coffee", BigDecimal("12.34"))),
        )
        val result = (CalculatorEngine().calculate(input) as CalculationOutcome.Success).result

        val id = repository.save(result, "Coffee run")
        val records = repository.observeHistory().first()

        assertEquals(1, records.size)
        assertEquals(id, records.single().id)
        assertEquals(1234L, records.single().createdAtEpochMillis)
        assertEquals("Coffee run", records.single().label)
        assertEquals(BigDecimal("12.34"), records.single().total)
    }

    @Test
    fun `normalizes and bounds saved history labels`() = runTest {
        val dao = FakeHistoryDao()
        val repository = HistoryRepository(dao)
        val result = (CalculatorEngine().calculate(CalculationInput(items = emptyList())) as CalculationOutcome.Success).result
        val oversized = "  ${"x".repeat(MAX_SAVED_NAME_CHARS + 25)}  "

        repository.save(result, oversized)

        assertEquals("x".repeat(MAX_SAVED_NAME_CHARS), repository.observeHistory().first().single().label)
    }

    @Test
    fun `blank history labels use a stable fallback`() = runTest {
        val dao = FakeHistoryDao()
        val repository = HistoryRepository(dao)
        val result = (CalculatorEngine().calculate(CalculationInput(items = emptyList())) as CalculationOutcome.Success).result

        repository.save(result, "   ")

        assertEquals("Calculation", repository.observeHistory().first().single().label)
    }

    @Test
    fun `snapshot returns a stable mapped history list`() = runTest {
        val dao = FakeHistoryDao()
        dao.upsertAll(listOf(sampleEntity("older", 100L), sampleEntity("newer", 200L)))
        val repository = HistoryRepository(dao)

        val snapshot = repository.snapshot()

        assertEquals(listOf("newer", "older"), snapshot.map { it.id })
    }

    @Test
    fun `purges entries older than cutoff`() = runTest {
        val dao = FakeHistoryDao()
        dao.upsert(sampleEntity("old", 100L))
        dao.upsert(sampleEntity("new", 500L))
        val repository = HistoryRepository(dao)

        val count = repository.purgeOlderThan(300L)
        val records = repository.observeHistory().first()

        assertEquals(1, count)
        assertEquals(listOf("new"), records.map { it.id })
    }

    @Test
    fun `clear removes all entries`() = runTest {
        val dao = FakeHistoryDao()
        dao.upsert(sampleEntity("a", 1L))
        dao.upsert(sampleEntity("b", 2L))
        val repository = HistoryRepository(dao)

        repository.clear()

        assertTrue(repository.observeHistory().first().isEmpty())
    }

    @Test
    fun `restore re-inserts the same history record`() = runTest {
        val dao = FakeHistoryDao()
        val repository = HistoryRepository(dao)
        val record = sampleRecord("undo", 900L)

        repository.restore(record)
        val restored = repository.observeHistory().first().single()

        assertEquals(record, restored)
    }

    private fun sampleRecord(id: String, createdAt: Long) = HistoryRecord(
        id = id,
        createdAtEpochMillis = createdAt,
        label = "Test",
        currencyCode = "INR",
        convertedCurrencyCode = "INR",
        subtotal = BigDecimal("10.00"),
        discountAmount = BigDecimal("0.00"),
        taxAmount = BigDecimal("0.00"),
        tipAmount = BigDecimal("0.00"),
        serviceChargeAmount = BigDecimal("0.00"),
        total = BigDecimal("10.00"),
        convertedTotal = BigDecimal("10.00"),
        perPerson = BigDecimal("10.00"),
        convertedPerPerson = BigDecimal("10.00"),
        splitCount = 1,
    )

    private fun sampleEntity(id: String, createdAt: Long) = HistoryEntity(
        id = id,
        createdAtEpochMillis = createdAt,
        label = "Test",
        currencyCode = "INR",
        convertedCurrencyCode = "INR",
        subtotal = "10.00",
        discountAmount = "0.00",
        taxAmount = "0.00",
        tipAmount = "0.00",
        serviceChargeAmount = "0.00",
        total = "10.00",
        convertedTotal = "10.00",
        perPerson = "10.00",
        convertedPerPerson = "10.00",
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
            val before = entries.value
            entries.value = before.filterNot { it.createdAtEpochMillis < cutoffEpochMillis }
            return before.size - entries.value.size
        }
    }
}
