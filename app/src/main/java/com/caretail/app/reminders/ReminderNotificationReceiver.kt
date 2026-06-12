package com.caretail.app.reminders

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.caretail.app.MainActivity
import com.caretail.app.R
import com.caretail.app.data.local.entities.ReminderEntity

class ReminderNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = ReminderNotificationScheduler.reminderIdFromIntent(intent)
        val petName = ReminderNotificationScheduler.petNameFromIntent(intent)
        val reminderTitle = ReminderNotificationScheduler.titleFromIntent(intent)
        val repeatType = ReminderNotificationScheduler.repeatTypeFromIntent(intent)
        val dueAtMillis = ReminderNotificationScheduler.dueAtMillisFromIntent(intent)

        if (reminderId <= 0L || reminderTitle.isBlank()) return

        val notificationPreferences = NotificationPreferences(context.applicationContext)
        if (notificationPreferences.areCareRemindersEnabled() && canPostNotifications(context)) {
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val contentIntent = PendingIntent.getActivity(
                context,
                reminderId.toInt(),
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            val notification = NotificationCompat.Builder(context, ReminderNotificationChannelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("CareTail reminder")
                .setContentText("$petName: $reminderTitle")
                .setStyle(NotificationCompat.BigTextStyle().bigText("$petName: $reminderTitle"))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            postNotification(context, reminderId, notification)
        }

        val reminder = ReminderEntity(
            id = reminderId,
            petId = 0L,
            title = reminderTitle,
            type = "Other",
            notes = null,
            dueAtMillis = dueAtMillis,
            repeatType = repeatType,
            isCompleted = false,
            completedAtMillis = null,
            createdAtMillis = dueAtMillis,
            updatedAtMillis = dueAtMillis,
        )
        ReminderNotificationScheduler(context.applicationContext, notificationPreferences).scheduleNextRepeatIfNeeded(reminder, petName)
    }

    private fun canPostNotifications(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun postNotification(
        context: Context,
        reminderId: Long,
        notification: android.app.Notification,
    ) {
        runCatching {
            NotificationManagerCompat.from(context).notify(reminderId.toInt(), notification)
        }
    }
}
