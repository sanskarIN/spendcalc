package `in`.sanskar.spendcalc

import android.content.Context
import androidx.room.Room
import `in`.sanskar.spendcalc.data.BackupRepository
import `in`.sanskar.spendcalc.data.HistoryRepository
import `in`.sanskar.spendcalc.data.SettingsRepository
import `in`.sanskar.spendcalc.data.TemplateRepository
import `in`.sanskar.spendcalc.data.local.SpendCalcDatabase
import `in`.sanskar.spendcalc.domain.CalculatorEngine
import `in`.sanskar.spendcalc.domain.export.BackupCodec

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
    val templateRepository: TemplateRepository by lazy {
        TemplateRepository(
            dao = database.templateDao(),
            calculatorEngine = calculatorEngine,
        )
    }
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(appContext) }
    val backupCodec: BackupCodec by lazy { BackupCodec() }
    val backupRepository: BackupRepository by lazy {
        BackupRepository(
            database = database,
            historyRepository = historyRepository,
            templateRepository = templateRepository,
            settingsRepository = settingsRepository,
        )
    }
}
