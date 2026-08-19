package `in`.sanskar.spendcalc.data

import `in`.sanskar.spendcalc.data.local.HistoryDao
import `in`.sanskar.spendcalc.data.local.HistoryEntity
import `in`.sanskar.spendcalc.data.local.TemplateDao
import `in`.sanskar.spendcalc.data.local.TemplateEntity
import `in`.sanskar.spendcalc.domain.model.CalculationInput
import `in`.sanskar.spendcalc.domain.model.CalculationTemplate
import `in`.sanskar.spendcalc.domain.model.HistoryRecord
import java.math.BigDecimal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RepositoryDuplicateIdTest {
    @Test
    fun `history replace all rejects duplicate IDs before clearing existing data`() = runTest {
        val dao = FakeHistoryDao()
        val repository = HistoryRepository(dao)
        repository.restore(history("existing", 1L))
        val duplicate = history("duplicate", 2L)

        val failure = runCatching {
            repository.replaceAll(listOf(duplicate, duplicate.copy(createdAtEpochMillis = 3L)))
        }.exceptionOrNull()

        assertTrue(failure is IllegalArgumentException)
        assertEquals(listOf("existing"), repository.observeHistory().first().map { it.id })
    }

    @Test
    fun `template replace all rejects duplicate IDs before clearing existing data`() = runTest {
        val dao = FakeTemplateDao()
        val repository = TemplateRepository(dao)
        repository.save("Existing", CalculationInput(items = emptyList()))
        val duplicate = template("duplicate", "First", 2L)

        val failure = runCatching {
            repository.replaceAll(listOf(duplicate, duplicate.copy(name = "Second", createdAtEpochMillis = 3L)))
        }.exceptionOrNull()

        assertTrue(failure is IllegalArgumentException)
        assertEquals(listOf("Existing"), repository.observeTemplates().first().map { it.name })
    }

    private fun history(id: String, createdAt: Long) = HistoryRecord(
        id = id,
        createdAtEpochMillis = createdAt,
        label = "Test",
        currencyCode = "INR",
        convertedCurrencyCode = "INR",
        subtotal = BigDecimal("10.00"),
        discountAmount = BigDecimal.ZERO,
        taxAmount = BigDecimal.ZERO,
        tipAmount = BigDecimal.ZERO,
        serviceChargeAmount = BigDecimal.ZERO,
        total = BigDecimal("10.00"),
        convertedTotal = BigDecimal("10.00"),
        perPerson = BigDecimal("10.00"),
        convertedPerPerson = BigDecimal("10.00"),
        splitCount = 1,
    )

    private fun template(id: String, name: String, createdAt: Long) = CalculationTemplate(
        id = id,
        name = name,
        createdAtEpochMillis = createdAt,
        discountPercent = BigDecimal.ZERO,
        taxPercent = BigDecimal("18"),
        tipPercent = BigDecimal.ZERO,
        serviceChargePercent = BigDecimal.ZERO,
        splitCount = 1,
        currencyCode = "INR",
        exchangeRate = BigDecimal.ONE,
        convertedCurrencyCode = "INR",
    )

    private class FakeHistoryDao : HistoryDao {
        private val state = MutableStateFlow<List<HistoryEntity>>(emptyList())

        override fun observeAll(): Flow<List<HistoryEntity>> = state
        override suspend fun snapshotAll(): List<HistoryEntity> = state.value

        override suspend fun upsert(entry: HistoryEntity) {
            state.value = (state.value.filterNot { it.id == entry.id } + entry)
                .sortedByDescending { it.createdAtEpochMillis }
        }

        override suspend fun upsertAll(entries: List<HistoryEntity>) {
            val ids = entries.mapTo(mutableSetOf()) { it.id }
            state.value = (state.value.filterNot { it.id in ids } + entries)
                .sortedByDescending { it.createdAtEpochMillis }
        }

        override suspend fun deleteById(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }

        override suspend fun clear() {
            state.value = emptyList()
        }

        override suspend fun deleteOlderThan(cutoffEpochMillis: Long): Int {
            val before = state.value
            state.value = before.filterNot { it.createdAtEpochMillis < cutoffEpochMillis }
            return before.size - state.value.size
        }
    }

    private class FakeTemplateDao : TemplateDao {
        private val state = MutableStateFlow<List<TemplateEntity>>(emptyList())

        override fun observeAll(): Flow<List<TemplateEntity>> = state
        override suspend fun snapshotAll(): List<TemplateEntity> = state.value

        override suspend fun upsert(template: TemplateEntity) {
            state.value = (state.value.filterNot { it.id == template.id } + template)
                .sortedBy { it.name.lowercase() }
        }

        override suspend fun upsertAll(templates: List<TemplateEntity>) {
            val ids = templates.mapTo(mutableSetOf()) { it.id }
            state.value = (state.value.filterNot { it.id in ids } + templates)
                .sortedBy { it.name.lowercase() }
        }

        override suspend fun deleteById(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }

        override suspend fun clear() {
            state.value = emptyList()
        }
    }
}
