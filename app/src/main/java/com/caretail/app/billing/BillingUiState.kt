package com.caretail.app.billing

enum class BillingStatus {
    Loading,
    ProductsLoaded,
    ProductsUnavailable,
    PurchaseInProgress,
    PurchaseSuccess,
    PurchasePending,
    PurchaseError,
    Restored,
    NotPremium,
}

data class BillingUiState(
    val status: BillingStatus = BillingStatus.Loading,
    val isLoading: Boolean = true,
    val products: List<PremiumProduct> = emptyList(),
    val errorMessage: String? = null,
    val isPremium: Boolean = false,
)
