package `in`.sanskar.spendcalc.data

import `in`.sanskar.spendcalc.data.local.TemplateDao
import `in`.sanskar.spendcalc.data.local.TemplateEntity
import `in`.sanskar.spendcalc.domain.model.CalculationInput
import `in`.sanskar.spendcalc.domain.model.CalculationTemplate
import `in`.sanskar.spendcalc.domain.model.MAX_SAVED_NAME_CHARS
import java.math.BigDecimal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class TemplateRepositoryTest {
    @Test
    fun `saves and restores reusable calculation settings`() = runTest {
        val dao = FakeTemplateDao()
        val repository = TemplateRepository(dao = dao, clock = { 42L })
        val input = CalculationInput(
            items = emptyList(),
            discountPercent = BigDecimal("5"),
            taxPercent = BigDecimal("18"),
            tipPercent = BigDecimal("3"),
            serviceChargePercent = BigDecimal("2"),
            splitCount = 4,
            currencyCode = "inr",
            exchangeRate = BigDecimal("0.0119"),
            convertedCurrencyCode = "usd",
        )

        val id = repository.save("Dinner", input)
        val template = repository.observeTemplates().first().single()

        assertEquals(id, template.id)
        assertEquals("Dinner", template.name)
        assertEquals(42L, template.createdAtEpochMillis)
        assertEquals(BigDecimal("18"), template.taxPercent)
        assertEquals(4, template.splitCount)
        assertEquals("INR", template.currencyCode)
        assertEquals("USD", template.convertedCurrencyCode)
        assertEquals(BigDecimal("0.0119"), template.exchangeRate)
    }

    @Test
    fun `normalizes and bounds saved template names`() = runTest {
        val dao = FakeTemplateDao()
        val repository = TemplateRepository(dao)
        val oversized = "  ${"t".repeat(MAX_SAVED_NAME_CHARS + 25)}  "

        repository.save(oversized, CalculationInput(items = emptyList()))

        assertEquals("t".repeat(MAX_SAVED_NAME_CHARS), repository.observeTemplates().first().single().name)
    }

    @Test
    fun `blank template names use a stable fallback`() = runTest {
        val dao = FakeTemplateDao()
        val repository = TemplateRepository(dao)

        repository.save("   ", CalculationInput(items = emptyList()))

        assertEquals("Template", repository.observeTemplates().first().single().name)
    }

    @Test
    fun `snapshot returns a stable mapped template list`() = runTest {
        val dao = FakeTemplateDao()
        val repository = TemplateRepository(dao)
        repository.restore(template("b", "Beta", 20L))
        repository.restore(template("a", "Alpha", 10L))

        val snapshot = repository.snapshot()

        assertEquals(listOf("Alpha", "Beta"), snapshot.map { it.name })
    }

    @Test
    fun `delete removes template`() = runTest {
        val dao = FakeTemplateDao()
        val repository = TemplateRepository(dao)
        val id = repository.save("One", CalculationInput(items = emptyList()))

        repository.delete(id)

        assertEquals(emptyList<TemplateEntity>(), dao.observeAll().first())
    }

    @Test
    fun `restore reinserts the exact deleted template`() = runTest {
        val dao = FakeTemplateDao()
        val repository = TemplateRepository(dao)
        val original = template("restore-me", "Restored", 123L)

        repository.restore(original)
        repository.delete(original.id)
        repository.restore(original)

        assertEquals(original, repository.observeTemplates().first().single())
    }

    @Test
    fun `replace all clears stale templates and restores backup templates`() = runTest {
        val dao = FakeTemplateDao()
        val repository = TemplateRepository(dao)
        repository.save("Old", CalculationInput(items = emptyList()))
        val restored = listOf(
            template("b", "Beta", 20L),
            template("a", "Alpha", 10L),
        )

        repository.replaceAll(restored)

        assertEquals(listOf("Alpha", "Beta"), repository.observeTemplates().first().map { it.name })
    }

    private fun template(id: String, name: String, createdAt: Long) = CalculationTemplate(
        id = id,
        name = name,
        createdAtEpochMillis = createdAt,
        discountPercent = BigDecimal("5"),
        taxPercent = BigDecimal("18"),
        tipPercent = BigDecimal("3"),
        serviceChargePercent = BigDecimal("2"),
        splitCount = 4,
        currencyCode = "INR",
        exchangeRate = BigDecimal("0.0119"),
        convertedCurrencyCode = "USD",
    )

    private class FakeTemplateDao : TemplateDao {
        private val templates = MutableStateFlow<List<TemplateEntity>>(emptyList())

        override fun observeAll(): Flow<List<TemplateEntity>> = templates

        override suspend fun snapshotAll(): List<TemplateEntity> = templates.value

        override suspend fun upsert(template: TemplateEntity) {
            templates.value = (templates.value.filterNot { it.id == template.id } + template)
                .sortedBy { it.name.lowercase() }
        }

        override suspend fun upsertAll(templates: List<TemplateEntity>) {
            val ids = templates.mapTo(mutableSetOf()) { it.id }
            this.templates.value = (this.templates.value.filterNot { it.id in ids } + templates)
                .sortedBy { it.name.lowercase() }
        }

        override suspend fun deleteById(id: String) {
            templates.value = templates.value.filterNot { it.id == id }
        }

        override suspend fun clear() {
            templates.value = emptyList()
        }
    }
}
