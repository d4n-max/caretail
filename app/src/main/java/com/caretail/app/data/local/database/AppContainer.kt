package com.caretail.app.data.local.database

import android.content.Context
import com.caretail.app.data.repository.HealthDiaryRepository
import com.caretail.app.data.repository.PetDocumentRepository
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.data.repository.ReminderRepository

class AppContainer(context: Context) {
    private val database = DatabaseProvider.getDatabase(context)

    val petRepository = PetRepository(database.petDao())
    val reminderRepository = ReminderRepository(database.reminderDao())
    val healthDiaryRepository = HealthDiaryRepository(database.healthDiaryDao())
    val petDocumentRepository = PetDocumentRepository(database.petDocumentDao())
}
