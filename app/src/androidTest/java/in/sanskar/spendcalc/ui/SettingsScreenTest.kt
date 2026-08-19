package `in`.sanskar.spendcalc.ui

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import `in`.sanskar.spendcalc.domain.model.UserPreferences
import `in`.sanskar.spendcalc.ui.screens.SettingsScreen
import `in`.sanskar.spendcalc.ui.theme.SpendCalcTheme
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun backupBusyStateIsVisibleAndDisablesBackupActions() {
        composeRule.setContent {
            SpendCalcTheme {
                SettingsScreen(
                    preferences = UserPreferences(onboardingCompleted = true),
                    onThemeModeChange = {},
                    onLargeTextChange = {},
                    onReducedMotionChange = {},
                    onAutoDeleteChange = {},
                    onExportBackup = {},
                    onRestoreBackup = {},
                    onAbout = {},
                    onOpenRepository = {},
                    backupBusy = true,
                )
            }
        }

        composeRule.onNodeWithText("Backup operation in progress…")
            .performScrollTo()
            .assertExists()
        composeRule.onNodeWithText("Export backup")
            .performScrollTo()
            .assertIsNotEnabled()
        composeRule.onNodeWithText("Restore backup")
            .performScrollTo()
            .assertIsNotEnabled()
    }
}
