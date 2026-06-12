package com.caretail.app.review

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReviewPromptEligibilityTest {
    private val eligibleState = ReviewPromptState(
        firstLaunchAtMillis = 1_000L,
        launchCount = 2,
        reminderCreatedCount = 2,
        reminderCompletedCount = 1,
        diaryEntrySavedCount = 0,
        lastReviewPromptAttemptAtMillis = 0L,
        reviewPromptAttemptCount = 0,
    )

    @Test
    fun eligibleAfterMeaningfulReminderCompletion() {
        assertTrue(
            ReviewPromptEligibility.isEligible(
                state = eligibleState,
                trigger = ReviewTrigger.ReminderCompleted,
                hasPetProfile = true,
                nowMillis = 1_000L + ReviewPromptEligibility.OneDayMillis,
            ),
        )
    }

    @Test
    fun firstLaunchIsNotEligible() {
        assertFalse(
            ReviewPromptEligibility.isEligible(
                state = eligibleState.copy(launchCount = 1),
                trigger = ReviewTrigger.ReminderCompleted,
                hasPetProfile = true,
                nowMillis = 1_000L + ReviewPromptEligibility.OneDayMillis,
            ),
        )
    }

    @Test
    fun recentAttemptIsThrottled() {
        val now = 1_000L + ReviewPromptEligibility.OneDayMillis
        assertFalse(
            ReviewPromptEligibility.isEligible(
                state = eligibleState.copy(lastReviewPromptAttemptAtMillis = now - 1_000L),
                trigger = ReviewTrigger.ReminderCompleted,
                hasPetProfile = true,
                nowMillis = now,
            ),
        )
    }

    @Test
    fun diarySaveCanQualifyAfterTimeAndLaunchThresholds() {
        assertTrue(
            ReviewPromptEligibility.isEligible(
                state = eligibleState.copy(
                    reminderCreatedCount = 0,
                    reminderCompletedCount = 0,
                    diaryEntrySavedCount = 1,
                ),
                trigger = ReviewTrigger.DiaryEntrySaved,
                hasPetProfile = true,
                nowMillis = 1_000L + ReviewPromptEligibility.OneDayMillis,
            ),
        )
    }
}
