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
class SpendCalcDatabaseTest {
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
    fun historyRoundTrip() = runBlocking {
        val entity = HistoryEntity(
            id = "history-1",
            createdAtEpochMillis = 100L,
            label = "Test",
            currencyCode = "INR",
            convertedCurrencyCode = "INR",
            subtotal = "12.00",
            discountAmount = "0.00",
            taxAmount = "0.00",
            tipAmount = "0.00",
            serviceChargeAmount = "0.00",
            total = "12.00",
            convertedTotal = "12.00",
            perPerson = "12.00",
            convertedPerPerson = "12.00",
            splitCount = 1,
        )

        database.historyDao().upsert(entity)

        assertEquals(listOf(entity), database.historyDao().observeAll().first())
    }

    @Test
    fun templateRoundTrip() = runBlocking {
        val entity = TemplateEntity(
            id = "template-1",
            name = "Lunch",
            createdAtEpochMillis = 200L,
            discountPercent = "0",
            taxPercent = "18",
            tipPercent = "5",
            serviceChargePercent = "0",
            splitCount = 2,
            currencyCode = "INR",
            exchangeRate = "1",
            convertedCurrencyCode = "INR",
        )

        database.templateDao().upsert(entity)

        assertEquals(listOf(entity), database.templateDao().observeAll().first())
    }
}
