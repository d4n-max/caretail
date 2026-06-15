package com.caretail.app.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthUiStateTest {
    @Test
    fun defaultStateIsSignedOut() {
        val state = AuthUiState()

        assertEquals(AuthStatus.SignedOut, state.status)
        assertFalse(state.isSignedIn)
    }

    @Test
    fun userStateIsSignedIn() {
        val state = AuthUiState(
            user = AuthUser(
                uid = "uid-1",
                displayName = "CareTail Tester",
                email = "tester@example.com",
                photoUrl = "https://example.com/photo.png",
            ),
        )

        assertEquals(AuthStatus.SignedIn, state.status)
        assertTrue(state.isSignedIn)
    }

    @Test
    fun loadingStateTakesPrecedence() {
        val state = AuthUiState(isLoading = true)

        assertEquals(AuthStatus.Loading, state.status)
    }

    @Test
    fun errorStateIsExplicit() {
        val state = AuthUiState(errorMessage = "Could not sign in.")

        assertEquals(AuthStatus.Error, state.status)
    }
}
