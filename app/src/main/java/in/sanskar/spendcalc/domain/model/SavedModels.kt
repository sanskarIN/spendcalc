package `in`.sanskar.spendcalc.domain.model

import java.math.BigDecimal

data class HistoryRecord(
    val id: String,
    val createdAtEpochMillis: Long,
    val label: String,
    val currencyCode: String,
    val convertedCurrencyCode: String,
    val subtotal: BigDecimal,
    val discountAmount: BigDecimal,
    val taxAmount: BigDecimal,
    val tipAmount: BigDecimal,
    val serviceChargeAmount: BigDecimal,
    val total: BigDecimal,
    val convertedTotal: BigDecimal,
    val perPerson: BigDecimal,
    val convertedPerPerson: BigDecimal,
    val splitCount: Int,
)

data class CalculationTemplate(
    val id: String,
    val name: String,
    val createdAtEpochMillis: Long,
    val discountPercent: BigDecimal,
    val taxPercent: BigDecimal,
    val tipPercent: BigDecimal,
    val serviceChargePercent: BigDecimal,
    val splitCount: Int,
    val currencyCode: String,
    val exchangeRate: BigDecimal,
    val convertedCurrencyCode: String,
)

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

enum class AutoDeleteHistory(val days: Int?) {
    NEVER(null),
    DAYS_30(30),
    DAYS_90(90),
}

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val largeText: Boolean = false,
    val reducedMotion: Boolean = false,
    val autoDeleteHistory: AutoDeleteHistory = AutoDeleteHistory.NEVER,
    val onboardingCompleted: Boolean = false,
)

data class SpendCalcBackup(
    val schemaVersion: Int = CURRENT_SCHEMA_VERSION,
    val exportedAtEpochMillis: Long,
    val history: List<HistoryRecord>,
    val templates: List<CalculationTemplate>,
    val preferences: UserPreferences,
) {
    companion object {
        const val CURRENT_SCHEMA_VERSION = 1
    }
}
