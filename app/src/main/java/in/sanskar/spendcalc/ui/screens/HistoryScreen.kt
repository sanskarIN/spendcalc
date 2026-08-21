package `in`.sanskar.spendcalc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import `in`.sanskar.spendcalc.R
import `in`.sanskar.spendcalc.domain.model.HistoryRecord
import `in`.sanskar.spendcalc.domain.model.truncateUtf16Safely
import `in`.sanskar.spendcalc.ui.components.EmptyState
import `in`.sanskar.spendcalc.ui.components.MoneyLine
import `in`.sanskar.spendcalc.ui.components.ScreenHeader
import `in`.sanskar.spendcalc.ui.theme.SpendCalcTokens
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private const val MAX_HISTORY_SEARCH_CHARS = 120

@Composable
fun HistoryScreen(
    history: List<HistoryRecord>,
    onDelete: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var confirmClear by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val normalizedQuery = searchQuery.trim()
    val filteredHistory = remember(history, normalizedQuery) {
        if (normalizedQuery.isBlank()) {
            history
        } else {
            history.filter { it.matches(normalizedQuery) }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text(stringResource(R.string.clear_history)) },
            text = { Text(stringResource(R.string.clear_history_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClear()
                        confirmClear = false
                        searchQuery = ""
                    },
                ) {
                    Text(stringResource(R.string.clear_history))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(SpendCalcTokens.SpaceMd),
        verticalArrangement = Arrangement.spacedBy(SpendCalcTokens.SpaceMd),
    ) {
        item {
            ScreenHeader(
                title = stringResource(R.string.history_title),
                subtitle = stringResource(R.string.history_privacy_note),
            )
        }

        if (history.isNotEmpty()) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = truncateUtf16Safely(it, MAX_HISTORY_SEARCH_CHARS)
                    },
                    label = { Text(stringResource(R.string.history_search_label)) },
                    supportingText = { Text(stringResource(R.string.history_search_supporting)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (normalizedQuery.isNotBlank()) {
                item {
                    TextButton(
                        onClick = { searchQuery = "" },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.clear_search))
                    }
                }
            }
            item {
                OutlinedButton(
                    onClick = { confirmClear = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.clear_history))
                }
            }
        }

        when {
            history.isEmpty() -> {
                item {
                    EmptyState(
                        title = stringResource(R.string.history_empty_title),
                        body = stringResource(R.string.history_empty_body),
                    )
                }
            }
            filteredHistory.isEmpty() -> {
                item {
                    EmptyState(
                        title = stringResource(R.string.history_no_results_title),
                        body = stringResource(R.string.history_no_results_body),
                    )
                }
            }
            else -> {
                items(filteredHistory, key = { it.id }) { entry ->
                    HistoryCard(entry = entry, onDelete = { onDelete(entry.id) })
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(
    entry: HistoryRecord,
    onDelete: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(SpendCalcTokens.SpaceMd),
            verticalArrangement = Arrangement.spacedBy(SpendCalcTokens.SpaceSm),
        ) {
            Text(
                text = entry.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = formatTimestamp(entry.createdAtEpochMillis),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            MoneyLine(
                label = stringResource(R.string.total),
                currencyCode = entry.currencyCode,
                amount = entry.total,
                emphasize = true,
            )
            if (entry.currencyCode != entry.convertedCurrencyCode || entry.convertedTotal != entry.total) {
                MoneyLine(
                    label = stringResource(R.string.converted_total),
                    currencyCode = entry.convertedCurrencyCode,
                    amount = entry.convertedTotal,
                )
            }
            MoneyLine(
                label = stringResource(R.string.per_person),
                currencyCode = entry.currencyCode,
                amount = entry.perPerson,
            )
            TextButton(onClick = onDelete) {
                Text(stringResource(R.string.delete_history))
            }
        }
    }
}

private fun HistoryRecord.matches(query: String): Boolean {
    val normalized = query.lowercase(Locale.ROOT)
    return sequenceOf(
        label,
        currencyCode,
        convertedCurrencyCode,
        subtotal.toPlainString(),
        total.toPlainString(),
        convertedTotal.toPlainString(),
        perPerson.toPlainString(),
        convertedPerPerson.toPlainString(),
    ).any { value -> value.lowercase(Locale.ROOT).contains(normalized) }
}

private fun formatTimestamp(epochMillis: Long): String {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())
    return formatter.format(Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()))
}
