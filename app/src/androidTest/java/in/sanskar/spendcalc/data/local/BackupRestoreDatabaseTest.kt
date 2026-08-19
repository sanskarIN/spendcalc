package `in`.sanskar.spendcalc.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BackupRestoreDatabaseTest {
    private lateinit var database: SpendCalcDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, SpendCalcDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun historyReplaceAllClearsExistingRows() = runBlocking {
        val dao = database.historyDao()
        dao.upsert(historyEntity("old", 1L))

        dao.replaceAll(listOf(historyEntity("new-a", 20L), historyEntity("new-b", 10L)))

        assertEquals(listOf("new-a", "new-b"), dao.observeAll().first().map { it.id })
    }

    @Test
    fun templateReplaceAllClearsExistingRows() = runBlocking {
        val dao = database.templateDao()
        dao.upsert(templateEntity("old", "Old"))

        dao.replaceAll(listOf(templateEntity("new-a", "Alpha"), templateEntity("new-b", "Beta")))

        assertEquals(listOf("new-a", "new-b"), dao.observeAll().first().map { it.id })
    }

    private fun historyEntity(id: String, createdAt: Long) = HistoryEntity(
        id = id,
        createdAtEpochMillis = createdAt,
        label = id,
        currencyCode = "INR",
        convertedCurrencyCode = "INR",
        subtotal = "1.00",
        discountAmount = "0.00",
        taxAmount = "0.00",
        tipAmount = "0.00",
        serviceChargeAmount = "0.00",
        total = "1.00",
        convertedTotal = "1.00",
        perPerson = "1.00",
        convertedPerPerson = "1.00",
        splitCount = 1,
    )

    private fun templateEntity(id: String, name: String) = TemplateEntity(
        id = id,
        name = name,
        createdAtEpochMillis = 1L,
        discountPercent = "0",
        taxPercent = "0",
        tipPercent = "0",
        serviceChargePercent = "0",
        splitCount = 1,
        currencyCode = "INR",
        exchangeRate = "1",
        convertedCurrencyCode = "INR",
    )
}
