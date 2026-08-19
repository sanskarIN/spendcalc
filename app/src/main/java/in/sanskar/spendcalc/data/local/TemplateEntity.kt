package in.sanskar.spendcalc.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_templates")
data class TemplateEntity(
    @PrimaryKey val id: String,
    val name: String,
    val createdAtEpochMillis: Long,
    val discountPercent: String,
    val taxPercent: String,
    val tipPercent: String,
    val serviceChargePercent: String,
    val splitCount: Int,
    val currencyCode: String,
    val exchangeRate: String,
    val convertedCurrencyCode: String,
)
