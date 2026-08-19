package `in`.sanskar.spendcalc.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import `in`.sanskar.spendcalc.platform.BackupFileIo
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ROUTE_CALCULATOR = "calculator"
private const val ROUTE_HISTORY = "history"
private const val ROUTE_TEMPLATES = "templates"
private const val ROUTE_SETTINGS = "settings"
private const val ROUTE_ABOUT = "about"
private const val BACKUP_MIME_TYPE = "application/vnd.spendcalc.backup"
private const val NAVIGATION_ANIMATION_MILLIS = 180

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
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }
    val csvFormatter = remember { CsvExportFormatter() }
    val receiptFormatter = remember { ReceiptTextFormatter() }
    val pdfExporter = remember { PdfReceiptExporter() }
    var pendingRestorePayload by remember { mutableStateOf<String?>(null) }
    var confirmRestore by remember { mutableStateOf(false) }

    val createBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(BACKUP_MIME_TYPE),
    ) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val payload = viewModel.createBackupPayload()
                    withContext(Dispatchers.IO) {
                        BackupFileIo.write(context, uri, payload)
                    }
                    viewModel.reportBackupExported()
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    viewModel.reportBackupFailure()
                }
            }
        }
    }

    val openBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    pendingRestorePayload = withContext(Dispatchers.IO) {
                        BackupFileIo.read(context, uri)
                    }
                    confirmRestore = true
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    viewModel.reportBackupFailure()
                }
            }
        }
    }

    if (confirmRestore) {
        AlertDialog(
            onDismissRequest = {
                confirmRestore = false
                pendingRestorePayload = null
            },
            title = { Text(stringResource(R.string.restore_backup_title)) },
            text = { Text(stringResource(R.string.restore_backup_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val payload = pendingRestorePayload
                        confirmRestore = false
                        pendingRestorePayload = null
                        if (payload != null) {
                            scope.launch { viewModel.restoreBackupPayload(payload) }
                        }
                    },
                ) {
                    Text(stringResource(R.string.confirm_restore))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        confirmRestore = false
                        pendingRestorePayload = null
                    },
                ) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        )
    }

    val feedbackText = when (calculator.feedback) {
        ActionFeedback.NONE -> ""
        ActionFeedback.HISTORY_SAVED -> stringResource(R.string.history_saved)
        ActionFeedback.TEMPLATE_SAVED -> stringResource(R.string.template_saved)
        ActionFeedback.HISTORY_DELETED -> stringResource(R.string.history_deleted)
        ActionFeedback.DELETED -> stringResource(R.string.entry_deleted)
        ActionFeedback.HISTORY_CLEARED -> stringResource(R.string.history_cleared)
        ActionFeedback.BACKUP_EXPORTED -> stringResource(R.string.backup_exported)
        ActionFeedback.BACKUP_RESTORED -> stringResource(R.string.backup_restored)
        ActionFeedback.BACKUP_FAILED -> stringResource(R.string.backup_failed)
    }

    LaunchedEffect(calculator.feedback) {
        if (calculator.feedback != ActionFeedback.NONE) {
            val historyDelete = calculator.feedback == ActionFeedback.HISTORY_DELETED
            val result = snackbarHostState.showSnackbar(
                message = feedbackText,
                actionLabel = if (historyDelete) context.getString(R.string.undo) else null,
            )
            if (historyDelete && result == SnackbarResult.ActionPerformed) {
                viewModel.undoDeleteHistory()
            }
            viewModel.consumeFeedback()
        }
    }

    fun showGenericError() {
        Toast.makeText(context, R.string.generic_error, Toast.LENGTH_SHORT).show()
    }

    fun openUrl(url: String) {
        if (!ExternalLinks.openUrl(context, url)) showGenericError()
    }

    fun exportBackup() {
        createBackupLauncher.launch(context.getString(R.string.backup_file_name))
    }

    fun restoreBackup() {
        openBackupLauncher.launch(
            arrayOf(
                BACKUP_MIME_TYPE,
                "application/octet-stream",
                "text/plain",
            ),
        )
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
        scope.launch {
            try {
                val file = withContext(Dispatchers.IO) {
                    ExportManager.createTextFile(
                        context = context,
                        fileName = "spendcalc-${System.currentTimeMillis()}.csv",
                        text = csvFormatter.format(input, result),
                    )
                }
                ExportManager.shareFile(
                    context = context,
                    chooserTitle = context.getString(R.string.share_chooser_title),
                    file = file,
                    mimeType = csvFormatter.mimeType,
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                Toast.makeText(context, R.string.export_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sharePdf() {
        val input = calculator.toCalculationInputOrNull() ?: return
        val result = calculator.result ?: return
        scope.launch {
            try {
                val file = withContext(Dispatchers.IO) {
                    pdfExporter.create(context, input, result)
                }
                ExportManager.shareFile(
                    context = context,
                    chooserTitle = context.getString(R.string.share_chooser_title),
                    file = file,
                    mimeType = "application/pdf",
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                Toast.makeText(context, R.string.export_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val enterTransition = if (preferences.reducedMotion) EnterTransition.None else fadeIn(
        animationSpec = tween(NAVIGATION_ANIMATION_MILLIS),
    )
    val exitTransition = if (preferences.reducedMotion) ExitTransition.None else fadeOut(
        animationSpec = tween(NAVIGATION_ANIMATION_MILLIS),
    )

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
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { enterTransition },
            popExitTransition = { exitTransition },
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
                    onExportBackup = ::exportBackup,
                    onRestoreBackup = ::restoreBackup,
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
