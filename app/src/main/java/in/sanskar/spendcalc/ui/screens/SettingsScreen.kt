package in.sanskar.spendcalc.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import in.sanskar.spendcalc.R
import in.sanskar.spendcalc.domain.model.AutoDeleteHistory
import in.sanskar.spendcalc.domain.model.ThemeMode
import in.sanskar.spendcalc.domain.model.UserPreferences
import in.sanskar.spendcalc.ui.components.ScreenHeader
import in.sanskar.spendcalc.ui.theme.SpendCalcTokens

@Composable
fun SettingsScreen(
    preferences: UserPreferences,
    onThemeModeChange: (ThemeMode) -> Unit,
    onLargeTextChange: (Boolean) -> Unit,
    onReducedMotionChange: (Boolean) -> Unit,
    onAutoDeleteChange: (AutoDeleteHistory) -> Unit,
    onAbout: () -> Unit,
    onOpenRepository: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(SpendCalcTokens.SpaceMd),
        verticalArrangement = Arrangement.spacedBy(SpendCalcTokens.SpaceMd),
    ) {
        ScreenHeader(
            title = stringResource(R.string.settings_title),
            subtitle = stringResource(R.string.settings_subtitle),
        )

        SettingsCard(title = stringResource(R.string.appearance_section)) {
            ThemeMode.entries.forEach { mode ->
                ChoiceRow(
                    label = when (mode) {
                        ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
                        ThemeMode.LIGHT -> stringResource(R.string.theme_light)
                        ThemeMode.DARK -> stringResource(R.string.theme_dark)
                    },
                    selected = preferences.themeMode == mode,
                    onClick = { onThemeModeChange(mode) },
                )
            }
        }

        SettingsCard(title = stringResource(R.string.accessibility_section)) {
            ToggleRow(
                title = stringResource(R.string.large_text),
                description = stringResource(R.string.large_text_description),
                checked = preferences.largeText,
                onCheckedChange = onLargeTextChange,
            )
            ToggleRow(
                title = stringResource(R.string.reduced_motion),
                description = stringResource(R.string.reduced_motion_description),
                checked = preferences.reducedMotion,
                onCheckedChange = onReducedMotionChange,
            )
        }

        SettingsCard(title = stringResource(R.string.data_section)) {
            Text(
                text = stringResource(R.string.privacy_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.privacy_summary),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.auto_delete_description),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            AutoDeleteHistory.entries.forEach { policy ->
                ChoiceRow(
                    label = when (policy) {
                        AutoDeleteHistory.NEVER -> stringResource(R.string.auto_delete_never)
                        AutoDeleteHistory.DAYS_30 -> stringResource(R.string.auto_delete_30_days)
                        AutoDeleteHistory.DAYS_90 -> stringResource(R.string.auto_delete_90_days)
                    },
                    selected = preferences.autoDeleteHistory == policy,
                    onClick = { onAutoDeleteChange(policy) },
                )
            }
        }

        SettingsCard(title = stringResource(R.string.project_updates)) {
            Text(
                text = stringResource(R.string.project_updates_description),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedButton(onClick = onOpenRepository, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.open_repository))
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onAbout),
        ) {
            Column(
                modifier = Modifier.padding(SpendCalcTokens.SpaceMd),
                verticalArrangement = Arrangement.spacedBy(SpendCalcTokens.SpaceXs),
            ) {
                Text(
                    text = stringResource(R.string.about),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = stringResource(R.string.made_by),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(SpendCalcTokens.SpaceMd),
            verticalArrangement = Arrangement.spacedBy(SpendCalcTokens.SpaceSm),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            content()
        }
    }
}

@Composable
private fun ChoiceRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(vertical = SpendCalcTokens.SpaceSm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpendCalcTokens.SpaceSm),
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpendCalcTokens.SpaceMd),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(SpendCalcTokens.SpaceXs),
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
