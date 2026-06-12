package com.caretail.app.review

import android.app.Activity
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

private const val ReviewPromptStoreName = "caretail_review_prompt"
private val Context.reviewPromptDataStore by preferencesDataStore(name = ReviewPromptStoreName)

class ReviewPromptManager(
    private val context: Context,
) {
    private val appContext = context.applicationContext

    suspend fun recordAppLaunch(nowMillis: Long = System.currentTimeMillis()) {
        appContext.reviewPromptDataStore.edit { preferences ->
            val firstLaunchAt = preferences[FirstLaunchAtMillisKey] ?: 0L
            if (firstLaunchAt == 0L) {
                preferences[FirstLaunchAtMillisKey] = nowMillis
            }
            preferences[LaunchCountKey] = (preferences[LaunchCountKey] ?: 0) + 1
        }
    }

    suspend fun onReminderCreated() {
        increment(ReminderCreatedCountKey)
    }

    suspend fun onReminderCompleted() {
        increment(ReminderCompletedCountKey)
    }

    suspend fun onDiaryEntrySaved() {
        increment(DiaryEntrySavedCountKey)
    }

    suspend fun requestReviewIfEligible(
        activity: Activity?,
        trigger: ReviewTrigger,
        hasPetProfile: Boolean,
        noBlockingUi: Boolean,
        nowMillis: Long = System.currentTimeMillis(),
    ): Boolean {
        if (activity == null || !noBlockingUi) return false

        val state = currentState()
        if (!ReviewPromptEligibility.isEligible(state, trigger, hasPetProfile, nowMillis)) return false

        recordPromptAttempt(nowMillis)

        return runCatching {
            val manager = ReviewManagerFactory.create(activity)
            val reviewInfo = manager.requestReviewFlow().await()
            manager.launchReviewFlow(activity, reviewInfo).await()
            true
        }.getOrDefault(false)
    }

    private suspend fun currentState(): ReviewPromptState {
        val preferences = appContext.reviewPromptDataStore.data.first()
        return ReviewPromptState(
            firstLaunchAtMillis = preferences[FirstLaunchAtMillisKey] ?: 0L,
            launchCount = preferences[LaunchCountKey] ?: 0,
            reminderCreatedCount = preferences[ReminderCreatedCountKey] ?: 0,
            reminderCompletedCount = preferences[ReminderCompletedCountKey] ?: 0,
            diaryEntrySavedCount = preferences[DiaryEntrySavedCountKey] ?: 0,
            lastReviewPromptAttemptAtMillis = preferences[LastReviewPromptAttemptAtMillisKey] ?: 0L,
            reviewPromptAttemptCount = preferences[ReviewPromptAttemptCountKey] ?: 0,
        )
    }

    private suspend fun increment(key: androidx.datastore.preferences.core.Preferences.Key<Int>) {
        appContext.reviewPromptDataStore.edit { preferences ->
            preferences[key] = (preferences[key] ?: 0) + 1
        }
    }

    private suspend fun recordPromptAttempt(nowMillis: Long) {
        appContext.reviewPromptDataStore.edit { preferences ->
            preferences[LastReviewPromptAttemptAtMillisKey] = nowMillis
            preferences[ReviewPromptAttemptCountKey] = (preferences[ReviewPromptAttemptCountKey] ?: 0) + 1
        }
    }

    private companion object {
        val FirstLaunchAtMillisKey = longPreferencesKey("first_launch_at_millis")
        val LaunchCountKey = intPreferencesKey("launch_count")
        val ReminderCreatedCountKey = intPreferencesKey("reminder_created_count")
        val ReminderCompletedCountKey = intPreferencesKey("reminder_completed_count")
        val DiaryEntrySavedCountKey = intPreferencesKey("diary_entry_saved_count")
        val LastReviewPromptAttemptAtMillisKey = longPreferencesKey("last_review_prompt_attempt_at_millis")
        val ReviewPromptAttemptCountKey = intPreferencesKey("review_prompt_attempt_count")
    }
}
