package in.sanskar.spendcalc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import in.sanskar.spendcalc.R
import in.sanskar.spendcalc.domain.model.CalculationResult
import in.sanskar.spendcalc.ui.CalculatorUiState
import in.sanskar.spendcalc.ui.FormIssue
import in.sanskar.spendcalc.ui.components.MoneyLine
import in.sanskar.spendcalc.ui.components.ScreenHeader
import in.sanskar.spendcalc.ui.theme.SpendCalcTokens

@Composable
fun CalculatorScreen(
    state: CalculatorUiState,
    onItemNameChange: (String, String) -> Unit,
    onItemAmountChange: (String, String) -> Unit,
    onAddItem: () -> Unit,
    onRemoveItem: (String) -> Unit,
    onDiscountChange: (String) -> Unit,
    onTaxChange: (String) -> Unit,
    onTipChange: (String) -> Unit,
    onServiceChargeChange: (String) -> Unit,
    onSplitCountChange: (String) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onExchangeRateChange: (String) -> Unit,
    onConvertedCurrencyChange: (String) -> Unit,
    onSaveHistory: () -> Unit,
    onSaveTemplate: (String) -> Unit,
    onReset: () -> Unit,
    onShareReceipt: () -> Unit,
    onShareCsv: () -> Unit,
    onSharePdf: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showTemplateDialog by rememberSaveable { mutableStateOf(false) }
    var templateName by rememberSaveable { mutableStateOf("") }

    if (showTemplateDialog) {
        AlertDialog(
            onDismissRequest = { showTemplateDialog = false },
            title = { Text(stringResource(R.string.save_template)) },
            text = {
                OutlinedTextField(
                    value = templateName,
                    onValueChange = { templateName = it },
                    label = { Text(stringResource(R.string.template_name)) },
                    singleLine = true,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveTemplate(templateName)
                        templateName = ""
                        showTemplateDialog = false
                    },
                    enabled = state.result != null,
                ) {
                    Text(stringResource(R.string.save_template))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTemplateDialog = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        )
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 840.dp
        if (isWide) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpendCalcTokens.SpaceLg),
                horizontalArrangement = Arrangement.spacedBy(SpendCalcTokens.SpaceLg),
                verticalAlignment = Alignment.Top,
            ) {
                CalculatorForm(
                    state = state,
                    onItemNameChange = onItemNameChange,
                    onItemAmountChange = onItemAmountChange,
                    onAddItem = onAddItem,
                    onRemoveItem = onRemoveItem,
                    onDiscountChange = onDiscountChange,
                    onTaxChange = onTaxChange,
                    onTipChange = onTipChange,
                    onServiceChargeChange = onServiceChargeChange,
                    onSplitCountChange = onSplitCountChange,
                    onCurrencyChange = onCurrencyChange,
                    onExchangeRateChange = onExchangeRateChange,
                    onConvertedCurrencyChange = onConvertedCurrencyChange,
                    onSaveHistory = onSaveHistory,
                    onSaveTemplate = { showTemplateDialog = true },
                    onReset = onReset,
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                )
                ReceiptCard(
                    result = state.result,
                    onShareReceipt = onShareReceipt,
                    onShareCsv = onShareCsv,
                    onSharePdf = onSharePdf,
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(max = 520.dp),
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(SpendCalcTokens.SpaceMd),
                verticalArrangement = Arrangement.spacedBy(SpendCalcTokens.SpaceLg),
            ) {
                CalculatorForm(
                    state = state,
                    onItemNameChange = onItemNameChange,
                    onItemAmountChange = onItemAmountChange,
                    onAddItem = onAddItem,
                    onRemoveItem = onRemoveItem,
                    onDiscountChange = onDiscountChange,
                    onTaxChange = onTaxChange,
                    onTipChange = onTipChange,
                    onServiceChargeChange = onServiceChargeChange,
                    onSplitCountChange = onSplitCountChange,
                    onCurrencyChange = onCurrencyChange,
                    onExchangeRateChange = onExchangeRateChange,
                    onConvertedCurrencyChange = onConvertedCurrencyChange,
                    onSaveHistory = onSaveHistory,
                    onSaveTemplate = { showTemplateDialog = true },
                    onReset = onReset,
                )
                ReceiptCard(
                    result = state.result,
                    onShareReceipt = onShareReceipt,
                    onShareCsv = onShareCsv,
                    onSharePdf = onSharePdf,
                )
                Spacer(Modifier.height(SpendCalcTokens.SpaceLg))
            }
        }
    }
}

@Composable
private fun CalculatorForm(
    state: CalculatorUiState,
    onItemNameChange: (String, String) -> Unit,
    onItemAmountChange: (String, String) -> Unit,
    onAddItem: () -> Unit,
    onRemoveItem: (String) -> Unit,
    onDiscountChange: (String) -> Unit,
    onTaxChange: (String) -> Unit,
    onTipChange: (String) -> Unit,
    onServiceChargeChange: (String) -> Unit,
    onSplitCountChange: (String) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onExchangeRateChange: (String) -> Unit,
    onConvertedCurrencyChange: (String) -> Unit,
    onSaveHistory: () -> Unit,
    onSaveTemplate: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpendCalcTokens.SpaceMd),
    ) {
        ScreenHeader(
            title = stringResource(R.string.calculator_title),
            subtitle = stringResource(R.string.calculator_subtitle),
        )

        state.items.forEachIndexed { index, item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
            ) {
                Column(
                    modifier = Modifier.padding(SpendCalcTokens.SpaceMd),
                    verticalArrangement = Arrangement.spacedBy(SpendCalcTokens.SpaceSm),
                ) {
                    Text(
                        text = "${stringResource(R.string.item_name)} ${index + 1}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    OutlinedTextField(
                        value = item.name,
                        onValueChange = { onItemNameChange(item.id, it) },
                        label = { Text(stringResource(R.string.item_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = item.amount,
                        onValueChange = { onItemAmountChange(item.id, it) },
                        label = { Text(stringResource(R.string.item_amount)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = FormIssue.ITEM_AMOUNT in state.issues,
                        supportingText = if (FormIssue.ITEM_AMOUNT in state.issues) {
                            { Text(stringResource(R.string.validation_amount)) }
                        } else null,
                    )
                    if (state.items.size > 1) {
                        TextButton(onClick = { onRemoveItem(item.id) }) {
                            Text(stringResource(R.string.remove_item))
                        }
                    }
                }
            }
        }

        OutlinedButton(onClick = onAddItem, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.add_item))
        }

        PercentageField(
            value = state.discountPercent,
            label = stringResource(R.string.discount_percent),
            isError = FormIssue.DISCOUNT in state.issues,
            onValueChange = onDiscountChange,
        )
        PercentageField(
            value = state.taxPercent,
            label = stringResource(R.string.tax_percent),
            isError = FormIssue.TAX in state.issues,
            onValueChange = onTaxChange,
        )
        PercentageField(
            value = state.tipPercent,
            label = stringResource(R.string.tip_percent),
            isError = FormIssue.TIP in state.issues,
            onValueChange = onTipChange,
        )
        PercentageField(
            value = state.serviceChargePercent,
            label = stringResource(R.string.service_percent),
            isError = FormIssue.SERVICE_CHARGE in state.issues,
            onValueChange = onServiceChargeChange,
        )

        OutlinedTextField(
            value = state.splitCount,
            onValueChange = onSplitCountChange,
            label = { Text(stringResource(R.string.split_count)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = FormIssue.SPLIT_COUNT in state.issues,
            supportingText = if (FormIssue.SPLIT_COUNT in state.issues) {
                { Text(stringResource(R.string.validation_split)) }
            } else null,
        )

        OutlinedTextField(
            value = state.currencyCode,
            onValueChange = onCurrencyChange,
            label = { Text(stringResource(R.string.currency_code)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = FormIssue.CURRENCY in state.issues,
            supportingText = if (FormIssue.CURRENCY in state.issues) {
                { Text(stringResource(R.string.validation_currency)) }
            } else null,
        )

        OutlinedTextField(
            value = state.exchangeRate,
            onValueChange = onExchangeRateChange,
            label = { Text(stringResource(R.string.exchange_rate)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = FormIssue.EXCHANGE_RATE in state.issues,
            supportingText = {
                Text(
                    if (FormIssue.EXCHANGE_RATE in state.issues) {
                        stringResource(R.string.validation_exchange_rate)
                    } else {
                        stringResource(R.string.exchange_rate_hint)
                    },
                )
            },
        )

        OutlinedTextField(
            value = state.convertedCurrencyCode,
            onValueChange = onConvertedCurrencyChange,
            label = { Text(stringResource(R.string.converted_currency_code)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = FormIssue.CONVERTED_CURRENCY in state.issues,
            supportingText = if (FormIssue.CONVERTED_CURRENCY in state.issues) {
                { Text(stringResource(R.string.validation_currency)) }
            } else null,
        )

        Button(
            onClick = onSaveHistory,
            enabled = state.result != null,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.save_to_history))
        }
        OutlinedButton(
            onClick = onSaveTemplate,
            enabled = state.result != null,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.save_template))
        }
        TextButton(onClick = onReset, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.reset))
        }
    }
}

@Composable
private fun PercentageField(
    value: String,
    label: String,
    isError: Boolean,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        isError = isError,
        supportingText = if (isError) {
            { Text(stringResource(R.string.validation_percent)) }
        } else null,
    )
}

@Composable
private fun ReceiptCard(
    result: CalculationResult?,
    onShareReceipt: () -> Unit,
    onShareCsv: () -> Unit,
    onSharePdf: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(SpendCalcTokens.SpaceLg),
            verticalArrangement = Arrangement.spacedBy(SpendCalcTokens.SpaceSm),
        ) {
            Text(
                text = stringResource(R.string.receipt_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            if (result == null) {
                Text(
                    text = stringResource(R.string.receipt_invalid_hint),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                return@Column
            }

            MoneyLine(stringResource(R.string.subtotal), result.currencyCode, result.subtotal)
            MoneyLine(stringResource(R.string.discount), result.currencyCode, result.discountAmount)
            MoneyLine(stringResource(R.string.tax), result.currencyCode, result.taxAmount)
            MoneyLine(stringResource(R.string.tip), result.currencyCode, result.tipAmount)
            MoneyLine(stringResource(R.string.service_charge), result.currencyCode, result.serviceChargeAmount)
            HorizontalDivider(modifier = Modifier.padding(vertical = SpendCalcTokens.SpaceXs))
            MoneyLine(stringResource(R.string.total), result.currencyCode, result.total, emphasize = true)
            if (result.currencyCode != result.convertedCurrencyCode || result.convertedTotal != result.total) {
                MoneyLine(
                    stringResource(R.string.converted_total),
                    result.convertedCurrencyCode,
                    result.convertedTotal,
                    emphasize = true,
                )
            }
            MoneyLine(stringResource(R.string.per_person), result.currencyCode, result.perPerson)
            if (result.currencyCode != result.convertedCurrencyCode || result.convertedPerPerson != result.perPerson) {
                MoneyLine(
                    stringResource(R.string.converted_per_person),
                    result.convertedCurrencyCode,
                    result.convertedPerPerson,
                )
            }

            Spacer(Modifier.height(SpendCalcTokens.SpaceSm))
            Button(onClick = onShareReceipt, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.share_receipt))
            }
            OutlinedButton(onClick = onShareCsv, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.share_csv))
            }
            OutlinedButton(onClick = onSharePdf, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.share_pdf))
            }
            Text(
                text = stringResource(R.string.made_by),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}
