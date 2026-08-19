package `in`.sanskar.spendcalc.ui

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import `in`.sanskar.spendcalc.R
import `in`.sanskar.spendcalc.domain.export.CsvExportFormatter
import `in`.sanskar.spendcalc.domain.export.ReceiptTextFormatter
import `in`.sanskar.spendcalc.domain.model.CalculationTemplate
import `in`.sanskar.spendcalc.domain.model.HistoryRecord
import `in`.sanskar.spendcalc.domain.model.UserPreferences
import `in`.sanskar.spendcalc.platform.ExportManager
import `in`.sanskar.spendcalc.platform.ExternalLinks
import `in`.sanskar.spendcalc.platform.PdfReceiptExporter
import `in`.sanskar.spendcalc.ui.screens.AboutScreen
import `in`.sanskar.spendcalc.ui.screens.CalculatorScreen
import `in`.sanskar.spendcalc.ui.screens.HistoryScreen
import `in`.sanskar.spendcalc.ui.screens.OnboardingScreen
import `in`.sanskar.spendcalc.ui.screens.SettingsScreen
import `in`.sanskar.spendcalc.ui.screens.TemplatesScreen
import `in`.sanskar.spendcalc.ui.theme.SpendCalcTheme

private const val ROUTE_CALCULATOR = "calculator"
private const val ROUTE_HISTORY = "history"
private const val ROUTE_TEMPLATES = "templates"
private const val ROUTE_SETTINGS = "settings"
private const val ROUTE_ABOUT = "about"

private data class NavigationDestination(
    val route: String,
    val labelResource: Int,
    val shortLabel: String,
)

private val primaryDestinations = listOf(
    NavigationDestination(ROUTE_CALCULATOR, R.string.nav_calculator, "="),
    NavigationDestination(ROUTE_HISTORY, R.string.nav_history, "H"),
    NavigationDestination(ROUTE_TEMPLATES, R.string.nav_templates, "T"),
    NavigationDestination(ROUTE_SETTINGS, R.string.nav_settings, "S"),
)

@Composable
fun SpendCalcApp(viewModel: SpendCalcViewModel) {
    val calculator by viewModel.calculator.collectAsStateWithLifecycle()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val templates by viewModel.templates.collectAsStateWithLifecycle()

    SpendCalcTheme(
        themeMode = preferences.themeMode,
        largeText = preferences.largeText,
    ) {
        if (!preferences.onboardingCompleted) {
            OnboardingScreen(onContinue = viewModel::completeOnboarding)
        } else {
            SpendCalcMainScaffold(
                viewModel = viewModel,
                calculator = calculator,
                history = history,
                templates = templates,
                preferences = preferences,
            )
        }
    }
}

@Composable
private fun SpendCalcMainScaffold(
    viewModel: SpendCalcViewModel,
    calculator: CalculatorUiState,
    history: List<HistoryRecord>,
    templates: List<CalculationTemplate>,
    preferences: UserPreferences,
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }
    val csvFormatter = remember { CsvExportFormatter() }
    val receiptFormatter = remember { ReceiptTextFormatter() }
    val pdfExporter = remember { PdfReceiptExporter() }

    val feedbackText = when (calculator.feedback) {
        ActionFeedback.NONE -> ""
        ActionFeedback.HISTORY_SAVED -> stringResource(R.string.history_saved)
        ActionFeedback.TEMPLATE_SAVED -> stringResource(R.string.template_saved)
        ActionFeedback.DELETED -> stringResource(R.string.entry_deleted)
        ActionFeedback.HISTORY_CLEARED -> stringResource(R.string.history_cleared)
    }

    LaunchedEffect(calculator.feedback) {
        if (calculator.feedback != ActionFeedback.NONE) {
            snackbarHostState.showSnackbar(feedbackText)
            viewModel.consumeFeedback()
        }
    }

    fun showGenericError() {
        Toast.makeText(context, R.string.generic_error, Toast.LENGTH_SHORT).show()
    }

    fun openUrl(url: String) {
        if (!ExternalLinks.openUrl(context, url)) showGenericError()
    }

    fun shareReceipt() {
        val input = calculator.toCalculationInputOrNull() ?: return
        val result = calculator.result ?: return
        runCatching {
            ExportManager.shareText(
                context = context,
                title = context.getString(R.string.share_chooser_title),
                text = receiptFormatter.format(input, result),
                mimeType = receiptFormatter.mimeType,
            )
        }.onFailure { showGenericError() }
    }

    fun shareCsv() {
        val input = calculator.toCalculationInputOrNull() ?: return
        val result = calculator.result ?: return
        runCatching {
            ExportManager.shareTextFile(
                context = context,
                chooserTitle = context.getString(R.string.share_chooser_title),
                fileName = "spendcalc-${System.currentTimeMillis()}.csv",
                mimeType = csvFormatter.mimeType,
                text = csvFormatter.format(input, result),
            )
        }.onFailure {
            Toast.makeText(context, R.string.export_failed, Toast.LENGTH_SHORT).show()
        }
    }

    fun sharePdf() {
        val input = calculator.toCalculationInputOrNull() ?: return
        val result = calculator.result ?: return
        runCatching {
            val file = pdfExporter.create(context, input, result)
            ExportManager.shareFile(
                context = context,
                chooserTitle = context.getString(R.string.share_chooser_title),
                file = file,
                mimeType = "application/pdf",
            )
        }.onFailure {
            Toast.makeText(context, R.string.export_failed, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                primaryDestinations.forEach { destination ->
                    val selected = currentRoute == destination.route ||
                        (currentRoute == ROUTE_ABOUT && destination.route == ROUTE_SETTINGS)
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(destination.shortLabel) },
                        label = { Text(stringResource(destination.labelResource)) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ROUTE_CALCULATOR,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(ROUTE_CALCULATOR) {
                CalculatorScreen(
                    state = calculator,
                    onItemNameChange = viewModel::updateItemName,
                    onItemAmountChange = viewModel::updateItemAmount,
                    onAddItem = viewModel::addItem,
                    onRemoveItem = viewModel::removeItem,
                    onDiscountChange = viewModel::updateDiscount,
                    onTaxChange = viewModel::updateTax,
                    onTipChange = viewModel::updateTip,
                    onServiceChargeChange = viewModel::updateServiceCharge,
                    onSplitCountChange = viewModel::updateSplitCount,
                    onCurrencyChange = viewModel::updateCurrencyCode,
                    onExchangeRateChange = viewModel::updateExchangeRate,
                    onConvertedCurrencyChange = viewModel::updateConvertedCurrencyCode,
                    onSaveHistory = { viewModel.saveHistory() },
                    onSaveTemplate = viewModel::saveTemplate,
                    onReset = viewModel::resetCalculator,
                    onShareReceipt = ::shareReceipt,
                    onShareCsv = ::shareCsv,
                    onSharePdf = ::sharePdf,
                )
            }
            composable(ROUTE_HISTORY) {
                HistoryScreen(
                    history = history,
                    onDelete = viewModel::deleteHistory,
                    onClear = viewModel::clearHistory,
                )
            }
            composable(ROUTE_TEMPLATES) {
                TemplatesScreen(
                    templates = templates,
                    onLoad = { template ->
                        viewModel.loadTemplate(template)
                        navController.navigate(ROUTE_CALCULATOR) {
                            popUpTo(ROUTE_CALCULATOR) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onDelete = viewModel::deleteTemplate,
                )
            }
            composable(ROUTE_SETTINGS) {
                SettingsScreen(
                    preferences = preferences,
                    onThemeModeChange = viewModel::setThemeMode,
                    onLargeTextChange = viewModel::setLargeText,
                    onReducedMotionChange = viewModel::setReducedMotion,
                    onAutoDeleteChange = viewModel::setAutoDeleteHistory,
                    onAbout = { navController.navigate(ROUTE_ABOUT) },
                    onOpenRepository = { openUrl(context.getString(R.string.repository_url)) },
                )
            }
            composable(ROUTE_ABOUT) {
                AboutScreen(
                    onOpenGitHub = { openUrl(context.getString(R.string.github_url)) },
                    onOpenRepository = { openUrl(context.getString(R.string.repository_url)) },
                    onOpenBmc = { openUrl(context.getString(R.string.bmc_url)) },
                    onBusinessEmailPrimary = {
                        if (!ExternalLinks.email(context, context.getString(R.string.business_email_primary))) {
                            showGenericError()
                        }
                    },
                    onBusinessEmailSecondary = {
                        if (!ExternalLinks.email(context, context.getString(R.string.business_email_secondary))) {
                            showGenericError()
                        }
                    },
                    onSupportEmail = {
                        if (!ExternalLinks.email(context, context.getString(R.string.support_email))) {
                            showGenericError()
                        }
                    },
                )
            }
        }
    }
}
