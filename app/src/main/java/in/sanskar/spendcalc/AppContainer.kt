package in.sanskar.spendcalc

import android.content.Context
import androidx.room.Room
import in.sanskar.spendcalc.data.HistoryRepository
import in.sanskar.spendcalc.data.SettingsRepository
import in.sanskar.spendcalc.data.TemplateRepository
import in.sanskar.spendcalc.data.local.SpendCalcDatabase
import in.sanskar.spendcalc.domain.CalculatorEngine

class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    private val database: SpendCalcDatabase by lazy {
        Room.databaseBuilder(
            appContext,
            SpendCalcDatabase::class.java,
            "spendcalc.db",
        ).build()
    }

    val calculatorEngine: CalculatorEngine by lazy { CalculatorEngine() }
    val historyRepository: HistoryRepository by lazy { HistoryRepository(database.historyDao()) }
    val templateRepository: TemplateRepository by lazy { TemplateRepository(database.templateDao()) }
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(appContext) }
}
