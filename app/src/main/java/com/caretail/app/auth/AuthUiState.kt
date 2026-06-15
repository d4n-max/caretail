package com.caretail.app.auth

enum class AuthStatus {
    SignedOut,
    Loading,
    SignedIn,
    Error,
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: AuthUser? = null,
    val errorMessage: String? = null,
) {
    val status: AuthStatus
        get() = when {
            isLoading -> AuthStatus.Loading
            errorMessage != null -> AuthStatus.Error
            user != null -> AuthStatus.SignedIn
            else -> AuthStatus.SignedOut
        }

    val isSignedIn: Boolean = user != null
}
