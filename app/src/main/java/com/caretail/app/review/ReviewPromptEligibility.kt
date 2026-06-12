package com.caretail.app.review

object ReviewPromptEligibility {
    const val OneDayMillis = 24L * 60L * 60L * 1_000L
    const val ReviewPromptThrottleMillis = 90L * OneDayMillis

    fun isEligible(
        state: ReviewPromptState,
        trigger: ReviewTrigger,
        hasPetProfile: Boolean,
        nowMillis: Long,
    ): Boolean {
        val firstLaunchAt = state.firstLaunchAtMillis.takeIf { it > 0L } ?: return false
        val enoughTimeHasPassed = nowMillis - firstLaunchAt >= OneDayMillis
        val throttleWindowPassed = state.lastReviewPromptAttemptAtMillis == 0L ||
            nowMillis - state.lastReviewPromptAttemptAtMillis >= ReviewPromptThrottleMillis
        val enoughPositiveActivity = state.reminderCreatedCount >= 2 || state.diaryEntrySavedCount >= 1
        val hasCompletedPositiveAction = state.reminderCompletedCount >= 1 || state.diaryEntrySavedCount >= 1
        return trigger in ReviewTrigger.entries &&
            hasPetProfile &&
            enoughPositiveActivity &&
            hasCompletedPositiveAction &&
            state.launchCount >= 2 &&
            enoughTimeHasPassed &&
            throttleWindowPassed
    }
}
