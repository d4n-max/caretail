package com.caretail.app.auth

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: AuthUser? = null,
    val errorMessage: String? = null,
) {
    val isSignedIn: Boolean = user != null
}
