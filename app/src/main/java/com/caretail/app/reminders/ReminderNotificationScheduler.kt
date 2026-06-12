package com.caretail.app.reminders

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.caretail.app.data.local.entities.ReminderEntity
import java.util.Calendar

const val ReminderNotificationChannelId = "caretail_reminders"

private const val ReminderExtraId = "reminder_id"
private const val ReminderExtraPetName = "pet_name"
private const val ReminderExtraTitle = "reminder_title"
private const val ReminderExtraRepeatType = "repeat_type"
private const val ReminderExtraDueAtMillis = "due_at_millis"

class ReminderNotificationScheduler(
    private val context: Context,
    private val notificationPreferences: NotificationPreferences = NotificationPreferences(context),
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleReminder(reminder: ReminderEntity, petName: String) {
        if (!notificationPreferences.areCareRemindersEnabled()) return
        if (reminder.isCompleted) return
        val triggerAtMillis = reminder.dueAtMillis.coerceAtLeast(System.currentTimeMillis() + 1_000L)
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            reminderPendingIntent(reminder, petName, PendingIntent.FLAG_UPDATE_CURRENT),
        )
    }

    fun cancelReminder(reminderId: Long) {
        alarmManager.cancel(cancelPendingIntent(reminderId))
    }

    fun rescheduleReminder(reminder: ReminderEntity, petName: String) {
        cancelReminder(reminder.id)
        scheduleReminder(reminder, petName)
    }

    fun scheduleNextRepeatIfNeeded(reminder: ReminderEntity, petName: String) {
        val nextDueAtMillis = nextRepeatDueAtMillis(reminder.dueAtMillis, reminder.repeatType) ?: return
        scheduleReminder(reminder.copy(dueAtMillis = nextDueAtMillis), petName)
    }

    private fun reminderPendingIntent(
        reminder: ReminderEntity,
        petName: String,
        updateFlag: Int,
    ): PendingIntent {
        val intent = Intent(context, ReminderNotificationReceiver::class.java).apply {
            putExtra(ReminderExtraId, reminder.id)
            putExtra(ReminderExtraPetName, petName)
            putExtra(ReminderExtraTitle, reminder.title)
            putExtra(ReminderExtraRepeatType, reminder.repeatType)
            putExtra(ReminderExtraDueAtMillis, reminder.dueAtMillis)
        }
        return PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            intent,
            updateFlag or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun cancelPendingIntent(reminderId: Long): PendingIntent {
        val intent = Intent(context, ReminderNotificationReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        ) ?: PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
            val channel = NotificationChannel(
                ReminderNotificationChannelId,
                "Care reminders",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Notifications for pet care reminders"
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        fun nextRepeatDueAtMillis(dueAtMillis: Long, repeatType: String): Long? {
            if (repeatType == "None") return null
            val now = System.currentTimeMillis()
            return Calendar.getInstance().apply {
                timeInMillis = dueAtMillis
                do {
                    when (repeatType) {
                        "Daily" -> add(Calendar.DAY_OF_YEAR, 1)
                        "Weekly" -> add(Calendar.DAY_OF_YEAR, 7)
                        "Monthly" -> add(Calendar.MONTH, 1)
                        "Yearly" -> add(Calendar.YEAR, 1)
                        else -> return null
                    }
                } while (timeInMillis <= now)
            }.timeInMillis
        }

        fun reminderIdFromIntent(intent: Intent): Long = intent.getLongExtra(ReminderExtraId, 0L)

        fun petNameFromIntent(intent: Intent): String = intent.getStringExtra(ReminderExtraPetName).orEmpty()

        fun titleFromIntent(intent: Intent): String = intent.getStringExtra(ReminderExtraTitle).orEmpty()

        fun repeatTypeFromIntent(intent: Intent): String = intent.getStringExtra(ReminderExtraRepeatType).orEmpty()

        fun dueAtMillisFromIntent(intent: Intent): Long = intent.getLongExtra(ReminderExtraDueAtMillis, 0L)
    }
}
