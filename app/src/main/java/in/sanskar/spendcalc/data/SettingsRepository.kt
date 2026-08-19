package in.sanskar.spendcalc.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import in.sanskar.spendcalc.domain.model.AutoDeleteHistory
import in.sanskar.spendcalc.domain.model.ThemeMode
import in.sanskar.spendcalc.domain.model.UserPreferences
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.spendCalcDataStore by preferencesDataStore(name = "spendcalc_settings")

class SettingsRepository(private val context: Context) {
    val preferences: Flow<UserPreferences> =
        context.spendCalcDataStore.data
            .catch { error ->
                if (error is IOException) emit(emptyPreferences()) else throw error
            }
            .map(::toUserPreferences)

    suspend fun setThemeMode(value: ThemeMode) {
        context.spendCalcDataStore.edit { it[Keys.THEME_MODE] = value.name }
    }

    suspend fun setLargeText(value: Boolean) {
        context.spendCalcDataStore.edit { it[Keys.LARGE_TEXT] = value }
    }

    suspend fun setReducedMotion(value: Boolean) {
        context.spendCalcDataStore.edit { it[Keys.REDUCED_MOTION] = value }
    }

    suspend fun setAutoDeleteHistory(value: AutoDeleteHistory) {
        context.spendCalcDataStore.edit { it[Keys.AUTO_DELETE] = value.name }
    }

    suspend fun setOnboardingCompleted(value: Boolean) {
        context.spendCalcDataStore.edit { it[Keys.ONBOARDING_COMPLETED] = value }
    }

    private fun toUserPreferences(values: Preferences): UserPreferences =
        UserPreferences(
            themeMode = enumOrDefault(values[Keys.THEME_MODE], ThemeMode.SYSTEM),
            largeText = values[Keys.LARGE_TEXT] ?: false,
            reducedMotion = values[Keys.REDUCED_MOTION] ?: false,
            autoDeleteHistory = enumOrDefault(values[Keys.AUTO_DELETE], AutoDeleteHistory.NEVER),
            onboardingCompleted = values[Keys.ONBOARDING_COMPLETED] ?: false,
        )

    private inline fun <reified T : Enum<T>> enumOrDefault(raw: String?, fallback: T): T =
        raw?.let { value -> enumValues<T>().firstOrNull { it.name == value } } ?: fallback

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LARGE_TEXT = booleanPreferencesKey("large_text")
        val REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
        val AUTO_DELETE = stringPreferencesKey("auto_delete_history")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
}
