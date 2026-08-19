package in.sanskar.spendcalc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import in.sanskar.spendcalc.BuildConfig
import in.sanskar.spendcalc.R
import in.sanskar.spendcalc.ui.components.ScreenHeader
import in.sanskar.spendcalc.ui.theme.SpendCalcTokens

@Composable
fun AboutScreen(
    onOpenGitHub: () -> Unit,
    onOpenRepository: () -> Unit,
    onOpenBmc: () -> Unit,
    onBusinessEmailPrimary: () -> Unit,
    onBusinessEmailSecondary: () -> Unit,
    onSupportEmail: () -> Unit,
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
            title = stringResource(R.string.about_title),
            subtitle = stringResource(R.string.about_description),
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(SpendCalcTokens.SpaceLg),
                verticalArrangement = Arrangement.spacedBy(SpendCalcTokens.SpaceSm),
            ) {
                Text(
                    text = "SpendCalc",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(stringResource(R.string.version_label, BuildConfig.VERSION_NAME))
                Text(stringResource(R.string.license_label))
                Text(
                    text = stringResource(R.string.made_by),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        AboutLinkButton(stringResource(R.string.repository_label), onOpenRepository)
        AboutLinkButton(stringResource(R.string.github_label), onOpenGitHub)
        AboutLinkButton(stringResource(R.string.bmc_label), onOpenBmc)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(SpendCalcTokens.SpaceMd),
                verticalArrangement = Arrangement.spacedBy(SpendCalcTokens.SpaceSm),
            ) {
                Text(
                    text = stringResource(R.string.contact_support_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                AboutLinkButton(stringResource(R.string.business_email_primary), onBusinessEmailPrimary)
                AboutLinkButton(stringResource(R.string.business_email_secondary), onBusinessEmailSecondary)
                AboutLinkButton(stringResource(R.string.support_email), onSupportEmail)
            }
        }

        Text(
            text = stringResource(R.string.about_privacy_footer),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AboutLinkButton(label: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(label)
    }
}
