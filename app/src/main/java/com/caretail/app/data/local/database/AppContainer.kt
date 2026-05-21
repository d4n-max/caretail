package com.caretail.app.data.local.database

import android.content.Context
import com.caretail.app.data.repository.HealthDiaryRepository
import com.caretail.app.data.repository.PetDocumentRepository
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.data.repository.ReminderRepository
import com.caretail.app.reminders.ReminderNotificationScheduler

class AppContainer(context: Context) {
    private val database = DatabaseProvider.getDatabase(context)
    private val appContext = context.applicationContext

    val petRepository = PetRepository(database.petDao())
    val reminderRepository = ReminderRepository(database.reminderDao())
    val reminderNotificationScheduler = ReminderNotificationScheduler(appContext)
    val healthDiaryRepository = HealthDiaryRepository(database.healthDiaryDao())
    val petDocumentRepository = PetDocumentRepository(database.petDocumentDao())
}
