package com.caretail.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.caretail.app.data.local.entities.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY dueAtMillis ASC")
    fun observeAllReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE petId = :petId ORDER BY dueAtMillis ASC")
    fun observeRemindersForPet(petId: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE isCompleted = 0 AND dueAtMillis >= :currentTimeMillis ORDER BY dueAtMillis ASC")
    fun observeUpcomingReminders(currentTimeMillis: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE dueAtMillis BETWEEN :startOfDayMillis AND :endOfDayMillis ORDER BY dueAtMillis ASC")
    fun observeTodayReminders(startOfDayMillis: Long, endOfDayMillis: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE isCompleted = 1 ORDER BY completedAtMillis DESC")
    fun observeCompletedReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    suspend fun getReminderById(id: Long): ReminderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("UPDATE reminders SET isCompleted = 1, completedAtMillis = :completedAtMillis, updatedAtMillis = :completedAtMillis WHERE id = :id")
    suspend fun markReminderCompleted(id: Long, completedAtMillis: Long)

    @Query("UPDATE reminders SET isCompleted = 0, completedAtMillis = NULL WHERE id = :id")
    suspend fun markReminderIncomplete(id: Long)
}
