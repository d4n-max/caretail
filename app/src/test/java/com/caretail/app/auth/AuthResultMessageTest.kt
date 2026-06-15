package com.caretail.app.auth

import org.junit.Assert.assertEquals
import org.junit.Test

class AuthResultMessageTest {
    @Test
    fun recentLoginRequiredUsesCalmUserMessage() {
        assertEquals(
            "Please sign in again before deleting your account.",
            AuthResultMessage.recentLoginRequired().message,
        )
    }

    @Test
    fun genericDeletionFailureDoesNotExposeInternals() {
        assertEquals(
            "Could not delete your account. Please try again.",
            AuthResultMessage.accountDeletionFailed().message,
        )
    }
}
