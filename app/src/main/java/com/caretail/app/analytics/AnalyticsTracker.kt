package com.caretail.app.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.caretail.app.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsTracker(context: Context) {
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context.applicationContext)

    fun trackScreenView(screen: String) {
        logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            Params.Screen to screen,
            FirebaseAnalytics.Param.SCREEN_NAME to screen,
        )
    }

    fun trackOnboardingStarted() {
        logEvent(Events.OnboardingStarted, Params.Screen to Screens.Onboarding)
    }

    fun trackOnboardingCompleted() {
        logEvent(Events.OnboardingCompleted, Params.Screen to Screens.Onboarding)
    }

    fun trackPaywallViewed(source: String, plan: String? = null) {
        logEvent(
            Events.PaywallViewed,
            Params.Screen to Screens.Premium,
            Params.Source to source,
            Params.Plan to plan,
        )
    }

    fun trackPremiumCtaClicked(screen: String, source: String, plan: String? = null) {
        logEvent(
            Events.PremiumCtaClicked,
            Params.Screen to screen,
            Params.Source to source,
            Params.Plan to plan,
        )
    }

    fun trackPurchaseStarted(plan: String, source: String) {
        logEvent(
            Events.PurchaseStarted,
            Params.Screen to Screens.Premium,
            Params.Source to source,
            Params.Plan to plan,
        )
    }

    fun trackPurchaseSuccess(plan: String, source: String) {
        logEvent(
            Events.PurchaseSuccess,
            Params.Screen to Screens.Premium,
            Params.Source to source,
            Params.Plan to plan,
            Params.Result to Results.Success,
        )
    }

    fun trackPurchaseFailed(plan: String?, source: String, result: String, errorType: String) {
        logEvent(
            Events.PurchaseFailed,
            Params.Screen to Screens.Premium,
            Params.Source to source,
            Params.Plan to plan,
            Params.Result to result,
            Params.ErrorType to errorType,
        )
    }

    fun trackSettingsOpened(source: String) {
        logEvent(
            Events.SettingsOpened,
            Params.Screen to Screens.Settings,
            Params.Source to source,
        )
    }

    private fun logEvent(name: String, vararg params: Pair<String, String?>) {
        val bundle = Bundle()
        params.forEach { (key, value) ->
            if (!value.isNullOrBlank()) {
                bundle.putString(key, value)
            }
        }
        firebaseAnalytics.logEvent(name, bundle)
        if (BuildConfig.DEBUG) {
            Log.d(Tag, "Analytics event=$name params=${bundle.toDebugMap()}")
        }
    }

    private fun Bundle.toDebugMap(): Map<String, String> =
        keySet().associateWith { key -> getString(key).orEmpty() }

    object Events {
        const val OnboardingStarted = "onboarding_started"
        const val OnboardingCompleted = "onboarding_completed"
        const val PaywallViewed = "paywall_viewed"
        const val PremiumCtaClicked = "premium_cta_clicked"
        const val PurchaseStarted = "purchase_started"
        const val PurchaseSuccess = "purchase_success"
        const val PurchaseFailed = "purchase_failed"
        const val SettingsOpened = "settings_opened"
    }

    object Params {
        const val Screen = "screen"
        const val Source = "source"
        const val Plan = "plan"
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
