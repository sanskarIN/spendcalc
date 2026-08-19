package in.sanskar.spendcalc.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_history")
data class HistoryEntity(
    @PrimaryKey val id: String,
    val createdAtEpochMillis: Long,
    val label: String,
    val currencyCode: String,
    val convertedCurrencyCode: String,
    val subtotal: String,
    val discountAmount: String,
    val taxAmount: String,
    val tipAmount: String,
    val serviceChargeAmount: String,
    val total: String,
    val convertedTotal: String,
    val perPerson: String,
    val convertedPerPerson: String,
    val splitCount: Int,
)
