package com.caretail.app.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController

@Composable
fun CareTailApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val onboardingPreferences = remember(context) { OnboardingPreferences(context.applicationContext) }
    CareTailNavGraph(
        navController = navController,
        startDestination = if (onboardingPreferences.hasSeenOnboarding()) {
            CareTailRoute.Home.route
        } else {
            CareTailRoute.Onboarding.route
        },
        onOnboardingCompleted = onboardingPreferences::markOnboardingSeen,
    )
}

private class OnboardingPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("caretail_onboarding", Context.MODE_PRIVATE)

    fun hasSeenOnboarding(): Boolean = sharedPreferences.getBoolean("has_seen_onboarding", false)

    fun markOnboardingSeen() {
        sharedPreferences.edit().putBoolean("has_seen_onboarding", true).apply()
    }
}
