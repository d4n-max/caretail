package com.caretail.app.billing

data class BillingUiState(
    val isLoading: Boolean = true,
    val products: List<PremiumProduct> = emptyList(),
    val errorMessage: String? = null,
    val isPremium: Boolean = false,
)
