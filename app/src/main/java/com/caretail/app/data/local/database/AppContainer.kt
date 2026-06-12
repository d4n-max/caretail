package com.caretail.app.data.local.database

import android.content.Context
import com.caretail.app.auth.AuthRepository
import com.caretail.app.billing.BillingRepository
import com.caretail.app.billing.PremiumEntitlementStore
import com.caretail.app.data.repository.HealthDiaryRepository
import com.caretail.app.data.repository.PetDocumentRepository
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.data.repository.ReminderRepository
import com.caretail.app.reminders.NotificationPreferences
import com.caretail.app.reminders.ReminderNotificationScheduler
import com.caretail.app.review.ReviewPromptManager

class AppContainer(context: Context) {
    private val database = DatabaseProvider.getDatabase(context)
    private val appContext = context.applicationContext

    val notificationPreferences = NotificationPreferences(appContext)
    val reviewPromptManager = ReviewPromptManager(appContext)
    val petRepository = PetRepository(database.petDao())
    val reminderRepository = ReminderRepository(database.reminderDao())
    val reminderNotificationScheduler = ReminderNotificationScheduler(appContext, notificationPreferences)
    val healthDiaryRepository = HealthDiaryRepository(database.healthDiaryDao())
    val petDocumentRepository = PetDocumentRepository(database.petDocumentDao())
    val premiumEntitlementStore = PremiumEntitlementStore(appContext)
    val billingRepository = BillingRepository(appContext, premiumEntitlementStore)
    val authRepository = AuthRepository(appContext)

    init {
        billingRepository.startConnection()
    }
}
