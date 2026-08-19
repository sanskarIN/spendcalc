package `in`.sanskar.spendcalc.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {
    @Query("SELECT * FROM calculation_templates ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<TemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(template: TemplateEntity)

    @Query("DELETE FROM calculation_templates WHERE id = :id")
    suspend fun deleteById(id: String)
}
