package com.caretail.app.billing

enum class PremiumPlan(
    val productId: String,
    val basePlanId: String,
    val label: String,
    val fallbackPrice: String,
) {
    Monthly(
        productId = "caretail_premium_monthly",
        basePlanId = "monthly",
        label = "Monthly",
        fallbackPrice = "$2.99/month",
    ),
    Yearly(
        productId = "caretail_premium_yearly",
        basePlanId = "yearly",
        label = "Yearly",
        fallbackPrice = "$19.99/year",
    ),
}
