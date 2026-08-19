package `in`.sanskar.spendcalc.data

import androidx.room.withTransaction
import `in`.sanskar.spendcalc.data.local.SpendCalcDatabase
import `in`.sanskar.spendcalc.domain.model.SpendCalcBackup
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class BackupRepository(
    private val database: SpendCalcDatabase,
    private val historyRepository: HistoryRepository,
    private val templateRepository: TemplateRepository,
    private val settingsRepository: SettingsRepository,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    suspend fun snapshot(): SpendCalcBackup {
        val (history, templates) = database.withTransaction {
            historyRepository.snapshot() to templateRepository.snapshot()
        }
        return SpendCalcBackup(
            exportedAtEpochMillis = clock(),
            history = history,
            templates = templates,
            preferences = settingsRepository.preferences.first(),
        )
    }

    suspend fun restore(backup: SpendCalcBackup) {
        require(backup.schemaVersion == SpendCalcBackup.CURRENT_SCHEMA_VERSION) {
            "Unsupported backup schema"
        }

        val previous = snapshot()
        try {
            replaceDatabaseData(backup)
            settingsRepository.replace(backup.preferences)
        } catch (error: Exception) {
            withContext(NonCancellable) {
                runCatching {
                    replaceDatabaseData(previous)
                    settingsRepository.replace(previous.preferences)
                }
            }
            throw error
        }
    }

    private suspend fun replaceDatabaseData(backup: SpendCalcBackup) {
        database.withTransaction {
            historyRepository.replaceAll(backup.history)
            templateRepository.replaceAll(backup.templates)
        }
    }
}
