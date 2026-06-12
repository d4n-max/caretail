package com.caretail.app.billing

import org.junit.Assert.assertEquals
import org.junit.Test

class PremiumPlanTest {
    @Test
    fun monthlyPlanMatchesPlayConsoleIds() {
        assertEquals("caretail_premium_monthly", PremiumPlan.Monthly.productId)
        assertEquals("monthly", PremiumPlan.Monthly.basePlanId)
    }

    @Test
    fun yearlyPlanMatchesPlayConsoleIds() {
        assertEquals("caretail_premium_yearly", PremiumPlan.Yearly.productId)
        assertEquals("yearly", PremiumPlan.Yearly.basePlanId)
    }
}
