package com.caretail.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.caretail.app.data.local.entities.HealthDiaryEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDiaryDao {
    @Query("SELECT * FROM health_diary_entries ORDER BY entryDateMillis DESC")
    fun observeAllEntries(): Flow<List<HealthDiaryEntryEntity>>

    @Query("SELECT * FROM health_diary_entries WHERE petId = :petId ORDER BY entryDateMillis DESC")
    fun observeEntriesForPet(petId: Long): Flow<List<HealthDiaryEntryEntity>>

    @Query("SELECT * FROM health_diary_entries WHERE petId = :petId ORDER BY entryDateMillis DESC LIMIT :limit")
    fun observeRecentEntriesForPet(petId: Long, limit: Int): Flow<List<HealthDiaryEntryEntity>>

    @Query("SELECT * FROM health_diary_entries WHERE id = :id LIMIT 1")
    suspend fun getEntryById(id: Long): HealthDiaryEntryEntity?

    @Query("SELECT COUNT(*) FROM health_diary_entries")
    suspend fun getEntryCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: HealthDiaryEntryEntity): Long

    @Update
    suspend fun updateEntry(entry: HealthDiaryEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: HealthDiaryEntryEntity)
}
