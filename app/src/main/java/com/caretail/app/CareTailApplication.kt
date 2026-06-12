package com.caretail.app

import android.app.Application
import com.caretail.app.reminders.ReminderNotificationScheduler
import com.caretail.app.review.ReviewPromptManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CareTailApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        ReminderNotificationScheduler.createNotificationChannel(this)
        applicationScope.launch {
            ReviewPromptManager(this@CareTailApplication).recordAppLaunch()
        }
    }
}
