package `in`.sanskar.spendcalc.data

import androidx.room.withTransaction
import `in`.sanskar.spendcalc.data.local.SpendCalcDatabase
import `in`.sanskar.spendcalc.domain.model.SpendCalcBackup

class BackupRepository(
    private val database: SpendCalcDatabase,
    private val historyRepository: HistoryRepository,
    private val templateRepository: TemplateRepository,
    private val settingsRepository: SettingsRepository,
) {
    suspend fun restore(backup: SpendCalcBackup) {
        require(backup.schemaVersion == SUPPORTED_SCHEMA_VERSION) { "Unsupported backup schema" }
        database.withTransaction {
            historyRepository.replaceAll(backup.history)
            templateRepository.replaceAll(backup.templates)
        }
        settingsRepository.replace(backup.preferences)
    }

    private companion object {
        const val SUPPORTED_SCHEMA_VERSION = 1
    }
}
