package `in`.sanskar.spendcalc.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [HistoryEntity::class, TemplateEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class SpendCalcDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun templateDao(): TemplateDao
}
