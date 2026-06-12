package com.caretail.app.review

data class ReviewPromptState(
    val firstLaunchAtMillis: Long = 0L,
    val launchCount: Int = 0,
    val reminderCreatedCount: Int = 0,
    val reminderCompletedCount: Int = 0,
    val diaryEntrySavedCount: Int = 0,
    val lastReviewPromptAttemptAtMillis: Long = 0L,
    val reviewPromptAttemptCount: Int = 0,
)
