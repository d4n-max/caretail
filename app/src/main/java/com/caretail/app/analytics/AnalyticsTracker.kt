package com.caretail.app.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.caretail.app.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsTracker(context: Context) {
    private val firebaseAnalytics = runCatching {
        FirebaseAnalytics.getInstance(context.applicationContext)
    }.getOrNull()

    fun trackScreenView(screen: String) {
        logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            Params.SourceScreen to screen,
            FirebaseAnalytics.Param.SCREEN_NAME to screen,
        )
    }

    fun trackOnboardingStarted() {
        logEvent(Events.OnboardingStarted, Params.SourceScreen to Screens.Onboarding)
    }

    fun trackOnboardingCompleted() {
        logEvent(Events.OnboardingCompleted, Params.SourceScreen to Screens.Onboarding)
    }

    fun trackPetCreated(sourceScreen: String, petCountAfterCreate: Int) {
        val petCountBucket = petCountBucket(petCountAfterCreate)
        logEvent(
            Events.PetCreated,
            Params.SourceScreen to sourceScreen,
            Params.PetCountBucket to petCountBucket,
        )
        if (petCountAfterCreate == 1) {
            logEvent(
                Events.FirstPetCreated,
                Params.SourceScreen to sourceScreen,
                Params.PetCountBucket to petCountBucket,
            )
        }
    }

    fun trackReminderCreated(sourceScreen: String, reminderType: String) {
        logEvent(
            Events.ReminderCreated,
            Params.SourceScreen to sourceScreen,
            Params.ReminderType to reminderType.analyticsValue(),
        )
    }

    fun trackReminderCompleted(sourceScreen: String, reminderType: String?) {
        logEvent(
            Events.ReminderCompleted,
            Params.SourceScreen to sourceScreen,
            Params.ReminderType to reminderType?.analyticsValue(),
        )
    }

    fun trackDiaryEntryCreated(sourceScreen: String, hasSymptoms: Boolean) {
        logEvent(
            Events.DiaryEntryCreated,
            Params.SourceScreen to sourceScreen,
            Params.DiaryHasSymptoms to hasSymptoms,
        )
    }

    fun trackDocumentAdded(sourceScreen: String, documentType: String) {
        logEvent(
            Events.DocumentAdded,
            Params.SourceScreen to sourceScreen,
            Params.DocumentType to documentType.analyticsValue(),
        )
    }

    fun trackExportReportClicked(sourceScreen: String) {
        logEvent(Events.ExportReportClicked, Params.SourceScreen to sourceScreen)
    }

    fun trackGoogleSignInStarted(sourceScreen: String) {
        logEvent(Events.GoogleSignInStarted, Params.SourceScreen to sourceScreen)
    }

    fun trackGoogleSignInCompleted(sourceScreen: String) {
        logEvent(
            Events.GoogleSignInCompleted,
            Params.SourceScreen to sourceScreen,
            Params.Success to true,
        )
    }

    fun trackGoogleSignInFailed(sourceScreen: String) {
        logEvent(
            Events.GoogleSignInFailed,
            Params.SourceScreen to sourceScreen,
            Params.Success to false,
        )
    }

    fun trackPaywallViewed(sourceScreen: String, paywallReason: String? = null, plan: String? = null) {
        logEvent(
            Events.PaywallViewed,
            Params.SourceScreen to sourceScreen,
            Params.PaywallReason to paywallReason,
            Params.PlanType to plan,
        )
    }

    fun trackUpgradeClicked(sourceScreen: String, paywallReason: String? = null, plan: String? = null) {
        logEvent(
            Events.UpgradeClicked,
            Params.SourceScreen to sourceScreen,
            Params.PaywallReason to paywallReason,
            Params.PlanType to plan,
        )
    }

    fun trackPurchaseStarted(plan: String, source: String) {
        logEvent(
            Events.PurchaseStarted,
            Params.SourceScreen to source,
            Params.PlanType to plan,
        )
    }

    fun trackPurchaseCompleted(plan: String, source: String) {
        logEvent(
            Events.PurchaseCompleted,
            Params.SourceScreen to source,
            Params.PlanType to plan,
            Params.Success to true,
        )
    }

    fun trackPurchaseFailed(plan: String?, source: String, result: String, errorType: String) {
        logEvent(
            Events.PurchaseFailed,
            Params.SourceScreen to source,
            Params.PlanType to plan,
            Params.Result to result,
            Params.ErrorType to errorType,
            Params.Success to false,
        )
    }

    fun trackRestorePurchasesClicked(sourceScreen: String) {
        logEvent(Events.RestorePurchasesClicked, Params.SourceScreen to sourceScreen)
    }

    fun trackSettingsOpened(source: String) {
        logEvent(
            Events.SettingsOpened,
            Params.SourceScreen to Screens.Settings,
            Params.Source to source,
        )
    }

    private fun logEvent(name: String, vararg params: Pair<String, Any?>) {
        val bundle = Bundle()
        params.forEach { (key, value) ->
            when (value) {
                is String -> if (value.isNotBlank()) bundle.putString(key, value)
                is Boolean -> bundle.putBoolean(key, value)
            }
        }
        runCatching { firebaseAnalytics?.logEvent(name, bundle) }
            .onFailure { error ->
                if (BuildConfig.DEBUG) {
                    Log.w(Tag, "Analytics event failed: $name", error)
                }
            }
        if (BuildConfig.DEBUG) {
            Log.d(Tag, "Analytics event=$name params=${bundle.toDebugMap()}")
        }
    }

    private fun Bundle.toDebugMap(): Map<String, Any?> =
        keySet().associateWith { key -> get(key) }

    private fun petCountBucket(count: Int): String = when {
        count <= 0 -> "0"
        count == 1 -> "1"
        count == 2 -> "2"
        count <= 4 -> "3_4"
        else -> "5_plus"
    }

    private fun String.analyticsValue(): String =
        trim()
            .lowercase()
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')

    object Events {
        const val OnboardingStarted = "onboarding_started"
        const val OnboardingCompleted = "onboarding_completed"
        const val FirstPetCreated = "first_pet_created"
        const val PetCreated = "pet_created"
        const val ReminderCreated = "reminder_created"
        const val ReminderCompleted = "reminder_completed"
        const val DiaryEntryCreated = "diary_entry_created"
        const val DocumentAdded = "document_added"
        const val PaywallViewed = "paywall_viewed"
        const val UpgradeClicked = "upgrade_clicked"
        const val PurchaseStarted = "purchase_started"
        const val PurchaseCompleted = "purchase_completed"
        const val PurchaseFailed = "purchase_failed"
        const val RestorePurchasesClicked = "restore_purchases_clicked"
        const val ExportReportClicked = "export_report_clicked"
        const val GoogleSignInStarted = "google_sign_in_started"
        const val GoogleSignInCompleted = "google_sign_in_completed"
        const val GoogleSignInFailed = "google_sign_in_failed"
        const val SettingsOpened = "settings_opened"
    }

    object Params {
        const val SourceScreen = "source_screen"
        const val Source = "source"
        const val PetCountBucket = "pet_count_bucket"
        const val ReminderType = "reminder_type"
        const val DiaryHasSymptoms = "diary_has_symptoms"
        const val DocumentType = "document_type"
        const val PaywallReason = "paywall_reason"
        const val PlanType = "plan_type"
        const val Success = "success"
        const val Result = "result"
        const val ErrorType = "error_type"
    }

    object Results {
        const val Success = "success"
        const val Canceled = "canceled"
        const val Failed = "failed"
        const val Unavailable = "unavailable"
        const val EmptyPurchase = "empty_purchase"
    }

    object Screens {
        const val Onboarding = "onboarding"
        const val Home = "home"
        const val Pets = "pets"
        const val PetProfile = "pet_profile"
        const val AddPet = "add_pet"
        const val EditPet = "edit_pet"
        const val Reminders = "reminders"
        const val AddReminder = "add_reminder"
        const val EditReminder = "edit_reminder"
        const val Diary = "diary"
        const val AddDiaryEntry = "add_diary_entry"
        const val EditDiaryEntry = "edit_diary_entry"
        const val Documents = "documents"
        const val AddDocument = "add_document"
        const val EditDocument = "edit_document"
        const val Premium = "premium"
        const val Settings = "settings"
    }

    companion object {
        private const val Tag = "AnalyticsTracker"
        const val SourceNavigation = "navigation"
        const val SourceOnboarding = "onboarding"
        const val SourcePaywall = "paywall"
        const val SourceBilling = "billing"
        const val SourceSettings = "settings"
        const val SourceUnknown = "unknown"
    }
}
