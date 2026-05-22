package com.caretail.app.billing

import com.android.billingclient.api.ProductDetails

data class PremiumProduct(
    val plan: PremiumPlan,
    val productId: String,
    val title: String,
    val description: String?,
    val price: String,
    val productDetails: ProductDetails,
    val offerToken: String?,
)
