package `in`.sanskar.spendcalc.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM calculation_history ORDER BY createdAtEpochMillis DESC")
    fun observeAll(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM calculation_history ORDER BY createdAtEpochMillis DESC")
    suspend fun snapshotAll(): List<HistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: HistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entries: List<HistoryEntity>)

    @Query("DELETE FROM calculation_history WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM calculation_history")
    suspend fun clear()

    @Query("DELETE FROM calculation_history WHERE createdAtEpochMillis < :cutoffEpochMillis")
    suspend fun deleteOlderThan(cutoffEpochMillis: Long): Int

    @Transaction
    suspend fun replaceAll(entries: List<HistoryEntity>) {
        clear()
        if (entries.isNotEmpty()) upsertAll(entries)
    }
}
