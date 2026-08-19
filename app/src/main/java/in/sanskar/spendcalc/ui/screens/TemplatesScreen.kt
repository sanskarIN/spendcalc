package `in`.sanskar.spendcalc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import `in`.sanskar.spendcalc.R
import `in`.sanskar.spendcalc.domain.model.CalculationTemplate
import `in`.sanskar.spendcalc.ui.components.EmptyState
import `in`.sanskar.spendcalc.ui.components.ScreenHeader
import `in`.sanskar.spendcalc.ui.theme.SpendCalcTokens

@Composable
fun TemplatesScreen(
    templates: List<CalculationTemplate>,
    onLoad: (CalculationTemplate) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(SpendCalcTokens.SpaceMd),
        verticalArrangement = Arrangement.spacedBy(SpendCalcTokens.SpaceMd),
    ) {
        item {
            ScreenHeader(
                title = stringResource(R.string.templates_title),
                subtitle = stringResource(R.string.templates_subtitle),
            )
        }

        if (templates.isEmpty()) {
            item {
                EmptyState(
                    title = stringResource(R.string.templates_empty_title),
                    body = stringResource(R.string.templates_empty_body),
                )
            }
        } else {
            items(templates, key = { it.id }) { template ->
                TemplateCard(
                    template = template,
                    onLoad = { onLoad(template) },
                    onDelete = { onDelete(template.id) },
                )
            }
        }
    }
}

@Composable
private fun TemplateCard(
    template: CalculationTemplate,
    onLoad: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(SpendCalcTokens.SpaceMd),
            verticalArrangement = Arrangement.spacedBy(SpendCalcTokens.SpaceSm),
        ) {
            Text(
                text = template.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            TemplateLine(stringResource(R.string.discount), "${template.discountPercent.toPlainString()}%")
            TemplateLine(stringResource(R.string.tax), "${template.taxPercent.toPlainString()}%")
            TemplateLine(stringResource(R.string.tip), "${template.tipPercent.toPlainString()}%")
            TemplateLine(stringResource(R.string.service_charge), "${template.serviceChargePercent.toPlainString()}%")
            TemplateLine(stringResource(R.string.split_count), template.splitCount.toString())
            TemplateLine(
                stringResource(R.string.currency_code),
                "${template.currencyCode} → ${template.convertedCurrencyCode}",
            )
            TemplateLine(stringResource(R.string.exchange_rate), template.exchangeRate.toPlainString())

            Button(onClick = onLoad, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.load_template))
            }
            TextButton(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.delete_template))
            }
        }
    }
}

@Composable
private fun TemplateLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium)
    }
}
