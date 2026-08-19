package `in`.sanskar.spendcalc.ui

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import `in`.sanskar.spendcalc.data.BackupRepository
import `in`.sanskar.spendcalc.data.HistoryRepository
import `in`.sanskar.spendcalc.data.SettingsRepository
import `in`.sanskar.spendcalc.data.TemplateRepository
import `in`.sanskar.spendcalc.data.local.SpendCalcDatabase
import `in`.sanskar.spendcalc.domain.CalculatorEngine
import `in`.sanskar.spendcalc.domain.export.BackupCodec
import `in`.sanskar.spendcalc.domain.model.UserPreferences
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SpendCalcJourneyTest {
    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var database: SpendCalcDatabase
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: SpendCalcViewModel

    @Before
    fun setUp() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, SpendCalcDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val historyRepository = HistoryRepository(database.historyDao())
        val templateRepository = TemplateRepository(database.templateDao())
        settingsRepository = SettingsRepository(context)
        settingsRepository.replace(UserPreferences(onboardingCompleted = true))
        viewModel = SpendCalcViewModel(
            calculatorEngine = CalculatorEngine(),
            historyRepository = historyRepository,
            templateRepository = templateRepository,
            settingsRepository = settingsRepository,
            backupRepository = BackupRepository(
                database = database,
                historyRepository = historyRepository,
                templateRepository = templateRepository,
                settingsRepository = settingsRepository,
            ),
            backupCodec = BackupCodec(),
        )
    }

    @After
    fun tearDown() = runBlocking {
        settingsRepository.replace(UserPreferences())
        database.close()
    }

    @Test
    fun calculateSaveAndOpenHistory() {
        composeRule.setContent {
            SpendCalcApp(viewModel)
        }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodes(hasSetTextAction()).fetchSemanticsNodes().size >= 2
        }
        composeRule.onAllNodes(hasSetTextAction())[1].performTextInput("100")
        composeRule.waitForIdle()

        composeRule.onAllNodesWithText("INR 100.00").assertCountEquals(3)
        composeRule.onNodeWithText("Save result").performClick()
        composeRule.onNodeWithText("History").performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Calculation").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Calculation").assertExists()
        composeRule.onNodeWithText("Delete history entry").assertExists()
    }
}
