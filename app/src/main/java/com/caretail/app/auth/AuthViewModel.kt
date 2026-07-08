package com.caretail.app.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.caretail.app.analytics.AnalyticsTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.observeCurrentUser().collect { user ->
                _uiState.update { it.copy(user = user, isLoading = false) }
            }
        }
    }

    fun signInWithGoogle(activity: Activity?) {
        analyticsTracker.trackGoogleSignInStarted(AnalyticsTracker.Screens.Settings)
        if (activity == null) {
            analyticsTracker.trackGoogleSignInFailed(AnalyticsTracker.Screens.Settings)
            _uiState.update { it.copy(errorMessage = "Could not sign in. Please try again.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.signInWithGoogle(activity)) {
                AuthResultMessage.Success -> {
                    analyticsTracker.trackGoogleSignInCompleted(AnalyticsTracker.Screens.Settings)
                    _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                }
                is AuthResultMessage.Error -> _uiState.update {
                    analyticsTracker.trackGoogleSignInFailed(AnalyticsTracker.Screens.Settings)
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun deleteAccount(onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.deleteAccount()) {
                AuthResultMessage.Success -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                    onDeleted()
                }
                is AuthResultMessage.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

class AuthViewModelFactory(
    private val authRepository: AuthRepository,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository, analyticsTracker) as T
        }
        error("Unknown ViewModel class: ${modelClass.name}")
    }
}
