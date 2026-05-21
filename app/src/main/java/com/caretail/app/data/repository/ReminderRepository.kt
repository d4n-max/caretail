package com.caretail.app.data.repository

import com.caretail.app.data.local.dao.ReminderDao
import com.caretail.app.data.local.entities.ReminderEntity
import kotlinx.coroutines.flow.Flow

class ReminderRepository(
    private val reminderDao: ReminderDao,
) {
    fun observeAllReminders(): Flow<List<ReminderEntity>> = reminderDao.observeAllReminders()

    fun observeRemindersForPet(petId: Long): Flow<List<ReminderEntity>> = reminderDao.observeRemindersForPet(petId)

    fun observeUpcomingReminders(currentTimeMillis: Long): Flow<List<ReminderEntity>> =
        reminderDao.observeUpcomingReminders(currentTimeMillis)

    fun observeTodayReminders(startOfDayMillis: Long, endOfDayMillis: Long): Flow<List<ReminderEntity>> =
        reminderDao.observeTodayReminders(startOfDayMillis, endOfDayMillis)

    fun observeCompletedReminders(): Flow<List<ReminderEntity>> = reminderDao.observeCompletedReminders()

    suspend fun getReminderById(id: Long): ReminderEntity? = reminderDao.getReminderById(id)

    suspend fun addReminder(reminder: ReminderEntity): Long = reminderDao.insertReminder(reminder)

    suspend fun updateReminder(reminder: ReminderEntity) = reminderDao.updateReminder(reminder)

    suspend fun deleteReminder(reminder: ReminderEntity) = reminderDao.deleteReminder(reminder)

    suspend fun markReminderCompleted(id: Long, completedAtMillis: Long) =
        reminderDao.markReminderCompleted(id, completedAtMillis)

    suspend fun markReminderIncomplete(id: Long, updatedAtMillis: Long) =
        reminderDao.markReminderIncomplete(id, updatedAtMillis)
}
