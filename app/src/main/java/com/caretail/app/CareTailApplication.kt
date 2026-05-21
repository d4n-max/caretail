package com.caretail.app

import android.app.Application
import com.caretail.app.reminders.ReminderNotificationScheduler

class CareTailApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ReminderNotificationScheduler.createNotificationChannel(this)
    }
}
