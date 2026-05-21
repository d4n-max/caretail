package com.caretail.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.caretail.app.data.local.database.AppContainer
import com.caretail.app.ui.screens.diary.AddDiaryEntryScreen
import com.caretail.app.ui.screens.diary.HealthDiaryScreen
import com.caretail.app.ui.screens.documents.AddDocumentScreen
import com.caretail.app.ui.screens.documents.DocumentsScreen
import com.caretail.app.ui.screens.home.HomeScreen
import com.caretail.app.ui.screens.onboarding.OnboardingScreen
import com.caretail.app.ui.screens.pets.AddPetScreen
import com.caretail.app.ui.screens.pets.PetProfileScreen
import com.caretail.app.ui.screens.pets.PetsScreen
import com.caretail.app.ui.screens.premium.PremiumScreen
import com.caretail.app.ui.screens.reminders.AddReminderScreen
import com.caretail.app.ui.screens.reminders.RemindersScreen
import com.caretail.app.ui.screens.settings.SettingsScreen

@Composable
fun CareTailNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val appContainer = remember(context) { AppContainer(context.applicationContext) }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val onBottomNavigate: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(CareTailRoute.Home.route) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = CareTailRoute.Onboarding.route,
        modifier = modifier,
    ) {
        composable(CareTailRoute.Onboarding.route) {
            OnboardingScreen(onGetStarted = { navController.navigate(CareTailRoute.Home.route) })
        }
        composable(CareTailRoute.Home.route) {
            HomeScreen(
                currentRoute = currentRoute,
                onNavigate = onBottomNavigate,
                petRepository = appContainer.petRepository,
                reminderRepository = appContainer.reminderRepository,
                healthDiaryRepository = appContainer.healthDiaryRepository,
                onOpenPremium = { navController.navigate(CareTailRoute.Premium.route) },
                onAddPet = { navController.navigate(CareTailRoute.AddPet.route) },
                onOpenPetProfile = { petId -> navController.navigate(CareTailRoute.PetProfile.createRoute(petId)) },
                onAddReminder = { navController.navigate(CareTailRoute.AddReminder.createRoute()) },
                onAddDiaryEntry = { navController.navigate(CareTailRoute.AddDiaryEntry.createRoute()) },
                onAddDocument = { navController.navigate(CareTailRoute.AddDocument.createRoute()) },
            )
        }
        composable(CareTailRoute.Pets.route) {
            PetsScreen(
                currentRoute = currentRoute,
                onNavigate = onBottomNavigate,
                petRepository = appContainer.petRepository,
                onOpenPetProfile = { petId -> navController.navigate(CareTailRoute.PetProfile.createRoute(petId)) },
                onAddPet = { navController.navigate(CareTailRoute.AddPet.route) },
                onOpenPremium = { navController.navigate(CareTailRoute.Premium.route) },
            )
        }
        composable(
            route = CareTailRoute.PetProfile.route,
            arguments = listOf(navArgument(CareTailRoute.PetProfile.petIdArg) { type = NavType.LongType }),
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getLong(CareTailRoute.PetProfile.petIdArg) ?: 0L
            PetProfileScreen(
                currentRoute = currentRoute,
                onNavigate = onBottomNavigate,
                petRepository = appContainer.petRepository,
                reminderRepository = appContainer.reminderRepository,
                healthDiaryRepository = appContainer.healthDiaryRepository,
                petDocumentRepository = appContainer.petDocumentRepository,
                petId = petId,
                onBack = { navController.popBackStack() },
                onAddReminder = { selectedPetId -> navController.navigate(CareTailRoute.AddReminder.createRoute(selectedPetId)) },
                onAddDiaryEntry = { selectedPetId -> navController.navigate(CareTailRoute.AddDiaryEntry.createRoute(selectedPetId)) },
                onAddDocument = { selectedPetId -> navController.navigate(CareTailRoute.AddDocument.createRoute(selectedPetId)) },
            )
        }
        composable(CareTailRoute.AddPet.route) {
            AddPetScreen(
                currentRoute = currentRoute,
                onNavigate = onBottomNavigate,
                petRepository = appContainer.petRepository,
                onBack = { navController.popBackStack() },
                onSaved = { petId ->
                    navController.navigate(CareTailRoute.PetProfile.createRoute(petId)) {
                        popUpTo(CareTailRoute.Pets.route)
                    }
                },
                onOpenPremium = { navController.navigate(CareTailRoute.Premium.route) },
            )
        }
        composable(CareTailRoute.Reminders.route) {
            RemindersScreen(
                currentRoute = currentRoute,
                onNavigate = onBottomNavigate,
                reminderRepository = appContainer.reminderRepository,
                reminderNotificationScheduler = appContainer.reminderNotificationScheduler,
                petRepository = appContainer.petRepository,
                onAddReminder = { navController.navigate(CareTailRoute.AddReminder.createRoute()) },
            )
        }
        composable(
            route = CareTailRoute.AddReminder.route,
            arguments = listOf(
                navArgument(CareTailRoute.AddReminder.petIdArg) {
                    type = NavType.LongType
                    defaultValue = -1L
                },
            ),
        ) { backStackEntry ->
            val petId = backStackEntry.arguments
                ?.getLong(CareTailRoute.AddReminder.petIdArg)
                ?.takeIf { it > 0L }
            AddReminderScreen(
                currentRoute = currentRoute,
                onNavigate = onBottomNavigate,
                petRepository = appContainer.petRepository,
                reminderRepository = appContainer.reminderRepository,
                reminderNotificationScheduler = appContainer.reminderNotificationScheduler,
                preselectedPetId = petId,
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.navigate(CareTailRoute.Reminders.route) {
                        popUpTo(CareTailRoute.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                },
                onAddPet = { navController.navigate(CareTailRoute.AddPet.route) },
            )
        }
        composable(CareTailRoute.Diary.route) {
            HealthDiaryScreen(
                currentRoute = currentRoute,
                onNavigate = onBottomNavigate,
                petRepository = appContainer.petRepository,
                healthDiaryRepository = appContainer.healthDiaryRepository,
                onAddDiaryEntry = { navController.navigate(CareTailRoute.AddDiaryEntry.createRoute()) },
                onAddPet = { navController.navigate(CareTailRoute.AddPet.route) },
            )
        }
        composable(
            route = CareTailRoute.AddDiaryEntry.route,
            arguments = listOf(
                navArgument(CareTailRoute.AddDiaryEntry.petIdArg) {
                    type = NavType.LongType
                    defaultValue = -1L
                },
            ),
        ) { backStackEntry ->
            val petId = backStackEntry.arguments
                ?.getLong(CareTailRoute.AddDiaryEntry.petIdArg)
                ?.takeIf { it > 0L }
            AddDiaryEntryScreen(
                currentRoute = currentRoute,
                onNavigate = onBottomNavigate,
                petRepository = appContainer.petRepository,
                healthDiaryRepository = appContainer.healthDiaryRepository,
                preselectedPetId = petId,
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.navigate(CareTailRoute.Diary.route) {
                        popUpTo(CareTailRoute.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                },
                onAddPet = { navController.navigate(CareTailRoute.AddPet.route) },
            )
        }
        composable(CareTailRoute.Documents.route) {
            DocumentsScreen(
                currentRoute = currentRoute,
                onNavigate = onBottomNavigate,
                petRepository = appContainer.petRepository,
                petDocumentRepository = appContainer.petDocumentRepository,
                onAddDocument = { navController.navigate(CareTailRoute.AddDocument.createRoute()) },
                onAddPet = { navController.navigate(CareTailRoute.AddPet.route) },
            )
        }
        composable(
            route = CareTailRoute.AddDocument.route,
            arguments = listOf(
                navArgument(CareTailRoute.AddDocument.petIdArg) {
                    type = NavType.LongType
                    defaultValue = -1L
                },
            ),
        ) { backStackEntry ->
            val petId = backStackEntry.arguments
                ?.getLong(CareTailRoute.AddDocument.petIdArg)
                ?.takeIf { it > 0L }
            AddDocumentScreen(
                currentRoute = currentRoute,
                onNavigate = onBottomNavigate,
                petRepository = appContainer.petRepository,
                petDocumentRepository = appContainer.petDocumentRepository,
                preselectedPetId = petId,
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.navigate(CareTailRoute.Documents.route) {
                        popUpTo(CareTailRoute.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                },
                onAddPet = { navController.navigate(CareTailRoute.AddPet.route) },
            )
        }
        composable(CareTailRoute.Premium.route) {
            PremiumScreen(onBack = { navController.popBackStack() })
        }
        composable(CareTailRoute.Settings.route) {
            SettingsScreen(
                currentRoute = currentRoute,
                onNavigate = onBottomNavigate,
                onOpenPremium = { navController.navigate(CareTailRoute.Premium.route) },
                onOpenDocuments = { navController.navigate(CareTailRoute.Documents.route) },
            )
        }
    }
}
