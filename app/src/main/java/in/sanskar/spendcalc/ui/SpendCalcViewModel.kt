package in.sanskar.spendcalc.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import in.sanskar.spendcalc.AppContainer
import in.sanskar.spendcalc.data.HistoryRepository
import in.sanskar.spendcalc.data.SettingsRepository
import in.sanskar.spendcalc.data.TemplateRepository
import in.sanskar.spendcalc.domain.CalculatorEngine
import in.sanskar.spendcalc.domain.model.AutoDeleteHistory
import in.sanskar.spendcalc.domain.model.CalculationError
import in.sanskar.spendcalc.domain.model.CalculationInput
import in.sanskar.spendcalc.domain.model.CalculationOutcome
import in.sanskar.spendcalc.domain.model.CalculationTemplate
import in.sanskar.spendcalc.domain.model.ExpenseItem
import in.sanskar.spendcalc.domain.model.ThemeMode
import in.sanskar.spendcalc.domain.model.UserPreferences
import java.math.BigDecimal
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SpendCalcViewModel(
    private val calculatorEngine: CalculatorEngine,
    private val historyRepository: HistoryRepository,
    private val templateRepository: TemplateRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _calculator = MutableStateFlow(CalculatorUiState())
    val calculator: StateFlow<CalculatorUiState> = _calculator

    val preferences: StateFlow<UserPreferences> = settingsRepository.preferences.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserPreferences(),
    )

    val history = historyRepository.observeHistory().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val templates = templateRepository.observeTemplates().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    init {
        recalculate()
        viewModelScope.launch {
            settingsRepository.preferences
                .map { it.autoDeleteHistory }
                .distinctUntilChanged()
                .collect(::applyAutoDeletePolicy)
        }
    }

    fun updateItemName(id: String, value: String) = updateCalculator { state ->
        state.copy(items = state.items.map { if (it.id == id) it.copy(name = value) else it })
    }

    fun updateItemAmount(id: String, value: String) = updateCalculator { state ->
        state.copy(items = state.items.map { if (it.id == id) it.copy(amount = value) else it })
    }

    fun addItem() = updateCalculator { state ->
        state.copy(items = state.items + ExpenseItemDraft())
    }

    fun removeItem(id: String) = updateCalculator { state ->
        val remaining = state.items.filterNot { it.id == id }
        state.copy(items = remaining.ifEmpty { listOf(ExpenseItemDraft()) })
    }

    fun updateDiscount(value: String) = updateCalculator { it.copy(discountPercent = value) }
    fun updateTax(value: String) = updateCalculator { it.copy(taxPercent = value) }
    fun updateTip(value: String) = updateCalculator { it.copy(tipPercent = value) }
    fun updateServiceCharge(value: String) = updateCalculator { it.copy(serviceChargePercent = value) }
    fun updateSplitCount(value: String) = updateCalculator { it.copy(splitCount = value) }
    fun updateCurrencyCode(value: String) = updateCalculator {
        it.copy(currencyCode = value.uppercase(Locale.ROOT))
    }
    fun updateExchangeRate(value: String) = updateCalculator { it.copy(exchangeRate = value) }
    fun updateConvertedCurrencyCode(value: String) = updateCalculator {
        it.copy(convertedCurrencyCode = value.uppercase(Locale.ROOT))
    }

    fun resetCalculator() {
        _calculator.value = CalculatorUiState()
        recalculate()
    }

    fun saveHistory(label: String = "Calculation") {
        val result = _calculator.value.result ?: return
        viewModelScope.launch {
            historyRepository.save(result, label)
            _calculator.value = _calculator.value.copy(feedback = ActionFeedback.HISTORY_SAVED)
        }
    }

    fun saveTemplate(name: String) {
        val state = _calculator.value
        if (state.result == null) return
        val parsed = parseForm(state)
        val input = parsed.input ?: return
        viewModelScope.launch {
            templateRepository.save(name, input)
            _calculator.value = _calculator.value.copy(feedback = ActionFeedback.TEMPLATE_SAVED)
        }
    }

    fun loadTemplate(template: CalculationTemplate) = updateCalculator { state ->
        state.copy(
            discountPercent = template.discountPercent.toPlainString(),
            taxPercent = template.taxPercent.toPlainString(),
            tipPercent = template.tipPercent.toPlainString(),
            serviceChargePercent = template.serviceChargePercent.toPlainString(),
            splitCount = template.splitCount.toString(),
            currencyCode = template.currencyCode,
            exchangeRate = template.exchangeRate.toPlainString(),
            convertedCurrencyCode = template.convertedCurrencyCode,
        )
    }

    fun deleteHistory(id: String) {
        viewModelScope.launch {
            historyRepository.delete(id)
            _calculator.value = _calculator.value.copy(feedback = ActionFeedback.DELETED)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.clear()
            _calculator.value = _calculator.value.copy(feedback = ActionFeedback.HISTORY_CLEARED)
        }
    }

    fun deleteTemplate(id: String) {
        viewModelScope.launch {
            templateRepository.delete(id)
            _calculator.value = _calculator.value.copy(feedback = ActionFeedback.DELETED)
        }
    }

    fun setThemeMode(value: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(value) }
    }

    fun setLargeText(value: Boolean) {
        viewModelScope.launch { settingsRepository.setLargeText(value) }
    }

    fun setReducedMotion(value: Boolean) {
        viewModelScope.launch { settingsRepository.setReducedMotion(value) }
    }

    fun setAutoDeleteHistory(value: AutoDeleteHistory) {
        viewModelScope.launch { settingsRepository.setAutoDeleteHistory(value) }
    }

    fun completeOnboarding() {
        viewModelScope.launch { settingsRepository.setOnboardingCompleted(true) }
    }

    fun consumeFeedback() {
        if (_calculator.value.feedback != ActionFeedback.NONE) {
            _calculator.value = _calculator.value.copy(feedback = ActionFeedback.NONE)
        }
    }

    private fun updateCalculator(transform: (CalculatorUiState) -> CalculatorUiState) {
        _calculator.value = transform(_calculator.value).copy(feedback = ActionFeedback.NONE)
        recalculate()
    }

    private fun recalculate() {
        val state = _calculator.value
        val parsed = parseForm(state)
        if (parsed.input == null) {
            _calculator.value = state.copy(result = null, issues = parsed.issues)
            return
        }

        when (val outcome = calculatorEngine.calculate(parsed.input)) {
            is CalculationOutcome.Success -> {
                _calculator.value = state.copy(result = outcome.result, issues = emptySet())
            }
            is CalculationOutcome.Failure -> {
                _calculator.value = state.copy(
                    result = null,
                    issues = parsed.issues + outcome.errors.map(::mapCalculationError),
                )
            }
        }
    }

    private fun parseForm(state: CalculatorUiState): ParsedForm {
        val issues = mutableSetOf<FormIssue>()
        val items = state.items.mapIndexed { index, draft ->
            val amount = parseDecimal(draft.amount, blankAsZero = true)
            if (amount == null) issues += FormIssue.ITEM_AMOUNT
            ExpenseItem(
                id = draft.id,
                name = draft.name.trim().ifBlank { "Item ${index + 1}" },
                amount = amount ?: BigDecimal.ZERO,
            )
        }

        val discount = parseDecimal(state.discountPercent, blankAsZero = true)
            ?: issue(issues, FormIssue.DISCOUNT)
        val tax = parseDecimal(state.taxPercent, blankAsZero = true)
            ?: issue(issues, FormIssue.TAX)
        val tip = parseDecimal(state.tipPercent, blankAsZero = true)
            ?: issue(issues, FormIssue.TIP)
        val service = parseDecimal(state.serviceChargePercent, blankAsZero = true)
            ?: issue(issues, FormIssue.SERVICE_CHARGE)
        val exchangeRate = parseDecimal(state.exchangeRate, blankAsZero = false)
            ?: issue(issues, FormIssue.EXCHANGE_RATE)
        val split = state.splitCount.trim().toIntOrNull()
        if (split == null) issues += FormIssue.SPLIT_COUNT

        if (issues.isNotEmpty()) return ParsedForm(null, issues)

        return ParsedForm(
            CalculationInput(
                items = items,
                discountPercent = discount!!,
                taxPercent = tax!!,
                tipPercent = tip!!,
                serviceChargePercent = service!!,
                splitCount = split!!,
                currencyCode = state.currencyCode,
                exchangeRate = exchangeRate!!,
                convertedCurrencyCode = state.convertedCurrencyCode,
            ),
            issues,
        )
    }

    private fun parseDecimal(raw: String, blankAsZero: Boolean): BigDecimal? {
        val text = raw.trim()
        if (text.isBlank()) return if (blankAsZero) BigDecimal.ZERO else null
        return text.toBigDecimalOrNull()
    }

    private fun issue(target: MutableSet<FormIssue>, issue: FormIssue): BigDecimal? {
        target += issue
        return null
    }

    private fun mapCalculationError(error: CalculationError): FormIssue = when (error) {
        is CalculationError.InvalidAmount -> FormIssue.ITEM_AMOUNT
        CalculationError.InvalidDiscount -> FormIssue.DISCOUNT
        CalculationError.InvalidTax -> FormIssue.TAX
        CalculationError.InvalidTip -> FormIssue.TIP
        CalculationError.InvalidServiceCharge -> FormIssue.SERVICE_CHARGE
        CalculationError.InvalidSplitCount -> FormIssue.SPLIT_COUNT
        CalculationError.InvalidCurrencyCode -> FormIssue.CURRENCY
        CalculationError.InvalidConvertedCurrencyCode -> FormIssue.CONVERTED_CURRENCY
        CalculationError.InvalidExchangeRate -> FormIssue.EXCHANGE_RATE
    }

    private suspend fun applyAutoDeletePolicy(policy: AutoDeleteHistory) {
        val days = policy.days ?: return
        val cutoff = System.currentTimeMillis() - days * MILLIS_PER_DAY
        historyRepository.purgeOlderThan(cutoff)
    }

    private data class ParsedForm(
        val input: CalculationInput?,
        val issues: Set<FormIssue>,
    )

    companion object {
        private const val MILLIS_PER_DAY = 86_400_000L

        fun factory(container: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    require(modelClass.isAssignableFrom(SpendCalcViewModel::class.java))
                    return SpendCalcViewModel(
                        calculatorEngine = container.calculatorEngine,
                        historyRepository = container.historyRepository,
                        templateRepository = container.templateRepository,
                        settingsRepository = container.settingsRepository,
                    ) as T
                }
            }
    }
}
