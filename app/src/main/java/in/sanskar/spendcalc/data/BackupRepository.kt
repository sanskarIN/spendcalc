package `in`.sanskar.spendcalc.data

import androidx.room.withTransaction
import `in`.sanskar.spendcalc.data.local.SpendCalcDatabase
import `in`.sanskar.spendcalc.domain.model.SpendCalcBackup
import kotlinx.coroutines.flow.first

class BackupRepository(
    private val database: SpendCalcDatabase,
    private val historyRepository: HistoryRepository,
    private val templateRepository: TemplateRepository,
    private val settingsRepository: SettingsRepository,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    suspend fun snapshot(): SpendCalcBackup =
        SpendCalcBackup(
            exportedAtEpochMillis = clock(),
            history = historyRepository.observeHistory().first(),
            templates = templateRepository.observeTemplates().first(),
            preferences = settingsRepository.preferences.first(),
        )

    suspend fun restore(backup: SpendCalcBackup) {
        require(backup.schemaVersion == SpendCalcBackup.CURRENT_SCHEMA_VERSION) {
            "Unsupported backup schema"
        }
        database.withTransaction {
            historyRepository.replaceAll(backup.history)
            templateRepository.replaceAll(backup.templates)
        }
        settingsRepository.replace(backup.preferences)
    }
}
