package `in`.sanskar.spendcalc.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {
    @Query("SELECT * FROM calculation_templates ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<TemplateEntity>>

    @Query("SELECT * FROM calculation_templates ORDER BY name COLLATE NOCASE ASC")
    suspend fun snapshotAll(): List<TemplateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(template: TemplateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(templates: List<TemplateEntity>)

    @Query("DELETE FROM calculation_templates WHERE id = :id")
    suspend fun deleteById(id: String)

    @Transaction
    suspend fun replaceAll(templates: List<TemplateEntity>) {
        clear()
        if (templates.isNotEmpty()) upsertAll(templates)
    }

    @Query("DELETE FROM calculation_templates")
    suspend fun clear()
}
