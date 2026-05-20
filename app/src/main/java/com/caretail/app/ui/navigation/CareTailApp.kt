package com.caretail.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun CareTailApp() {
    val navController = rememberNavController()
    CareTailNavGraph(navController = navController)
}
