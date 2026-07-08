package com.caretail.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.caretail.app.analytics.AnalyticsTracker
import com.caretail.app.auth.AuthViewModel
import com.caretail.app.auth.AuthViewModelFactory
import com.caretail.app.billing.PremiumUpsellReason
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
    startDestination: String = CareTailRoute.Onboarding.route,
    onOnboardingCompleted: () -> Unit = {},
) {
    val context = LocalContext.current
    val appContainer = remember(context) { AppContainer(context.applicationContext) }
    val authViewModel: AuthViewModel = viewModel(
        factory = remember(appContainer.authRepository, appContainer.analyticsTracker) {
            AuthViewModelFactory(appContainer.authRepository, appContainer.analyticsTracker)
        },
    )
    val authUiState = authViewModel.uiState.collectAsState().value
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val currentScreen = currentRoute?.analyticsScreenName() ?: AnalyticsTracker.Screens.Home
    val openPremium: (PremiumUpsellReason?, String, String) -> Unit = { reason, screen, source ->
        appContainer.analyticsTracker.trackUpgradeClicked(
            sourceScreen = screen,
            paywallReason = source,
        )
        navController.navigate(CareTailRoute.Premium.createRoute(reason))
    }
    val onBottomNavigate: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(CareTailRoute.Home.route) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val closePremium: () -> Unit = {
        if (navController.previousBackStackEntry != null) {
            navController.popBackStack()
        } else {
            navController.navigate(CareTailRoute.Home.route) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = false
                }
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(currentRoute) {
        val screen = currentRoute?.analyticsScreenName() ?: return@LaunchedEffect
        appContainer.analyticsTracker.trackScreenView(screen)
        when (screen) {
            AnalyticsTracker.Screens.Onboarding -> appContainer.analyticsTracker.trackOnboardingStarted()
            AnalyticsTracker.Screens.Settings -> appContainer.analyticsTracker.trackSettingsOpened(
                source = AnalyticsTracker.SourceNavigation,
            )
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(CareTailRoute.Onboarding.route) {
            OnboardingScreen(
                onGetStarted = {
                    appContainer.analyticsTracker.trackOnboardingCompleted()
                    onOnboardingCompleted()
                    navController.navigate(CareTailRoute.AddPet.route) {
                        popUpTo(CareTailRoute.Onboarding.route) { inclusive = true }
                    }
                },
            )
        }
        composable(CareTailRoute.Home.route) {
            HomeScreen(
                currentRoute = currentRoute,
                onNavigate = onBottomNavigate,
                petRepository = appContainer.petRepository,
                reminderRepository = appContainer.reminderRepository,
                healthDiaryRepository = appContainer.healthDiaryRepository,
                onOpenPremium = {
                    openPremium(null, AnalyticsTracker.Screens.Home, "home")
                },
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
                onOpenPremium = {
                    openPremium(PremiumUpsellReason.PetLimit, AnalyticsTracker.Screens.Pets, "pet_limit")
                },
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
                reminderNotificationScheduler = appContainer.reminderNotificationScheduler,
                petId = petId,
                onBack = { navController.popBackStack() },
                onAddReminder = { selectedPetId -> navController.navigate(CareTailRoute.AddReminder.createRoute(selectedPetId)) },
                onAddDiaryEntry = { selectedPetId -> navController.navigate(CareTailRoute.AddDiaryEntry.createRoute(selectedPetId)) },
                onAddDocument = { selectedPetId -> navController.navigate(CareTailRoute.AddDocument.createRoute(selectedPetId)) },
                onOpenPremium = { reason ->
                    openPremium(reason, AnalyticsTracker.Screens.PetProfile, reason.routeValue)
                },
                onEditPet = { selectedPetId -> navController.navigate(CareTailRoute.EditPet.createRoute(selectedPetId)) },
                onDeleted = {
                    navController.navigate(CareTailRoute.Pets.route) {
                        popUpTo(CareTailRoute.Home.route) { saveState = true }
                        launchSingleTop = true
                    }
                },
                analyticsTracker = appContainer.analyticsTracker,
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
                onOpenPremium = {
                    openPremium(PremiumUpsellReason.PetLimit, currentScreen, "pet_limit")
                },
                analyticsTracker = appContainer.analyticsTracker,
            )
        }
        composable(
            route = CareTailRoute.EditPet.route,
            arguments = listOf(navArgument(CareTailRoute.EditPet.petIdArg) { type = NavType.LongType }),
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getLong(CareTailRoute.EditPet.petIdArg) ?: 0L
            AddPetScreen(
                currentRoute = currentRoute,
                onNavigate = onBottomNavigate,
                petRepository = appContainer.petRepository,
                editPetId = petId,
                onBack = { navController.popBackStack() },
                onSaved = { savedPetId ->
                    navController.navigate(CareTailRoute.PetProfile.createRoute(savedPetId)) {
                        launchSingleTop = true
                    }
                },
                onOpenPremium = {
                    openPremium(PremiumUpsellReason.PetLimit, currentScreen, "pet_limit")
                },
                analyticsTracker = appContainer.analyticsTracker,
            )
        }
        composable(CareTailRoute.Reminders.route) {
            RemindersScreen(
                currentRoute = currentRoute,
                onNavigate = onBottomNavigate,
                reminderRepository = appContainer.reminderRepository,
                reminderNotificationScheduler = appContainer.reminderNotificationScheduler,
                petRepository = appContainer.petRepository,
                reviewPromptManager = appContainer.reviewPromptManager,
                onAddReminder = { navController.navigate(CareTailRoute.AddReminder.createRoute()) },
                onEditReminder = { reminderId -> navController.navigate(CareTailRoute.EditReminder.createRoute(reminderId)) },
                analyticsTracker = appContainer.analyticsTracker,
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
                notificationPreferences = appContainer.notificationPreferences,
                reviewPromptManager = appContainer.reviewPromptManager,
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
                onOpenPremium = { reason ->
                    openPremium(reason, currentScreen, reason.routeValue)
                },
                analyticsTracker = appContainer.analyticsTracker,
            )
        }
        composable(
            route = CareTailRoute.EditReminder.route,
            arguments = listOf(navArgument(CareTailRoute.EditReminder.reminderIdArg) { type = NavType.LongType }),
        ) { backStackEntry ->
            val reminderId = backStackEntry.arguments?.getLong(CareTailRoute.EditReminder.reminderIdArg) ?: 0L
            AddReminderScreen(
                currentRoute = currentRoute,
                onNavigate = onBottomNavigate,
                petRepository = appContainer.petRepository,
                reminderRepository = appContainer.reminderRepository,
                reminderNotificationScheduler = appContainer.reminderNotificationScheduler,
                notificationPreferences = appContainer.notificationPreferences,
                reviewPromptManager = appContainer.reviewPromptManager,
                preselectedPetId = null,
                editReminderId = reminderId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.navigate(CareTailRoute.Reminders.route) { launchSingleTop = true } },
                onAddPet = { navController.navigate(CareTailRoute.AddPet.route) },
                onOpenPremium = { reason ->
                    openPremium(reason, currentScreen, reason.routeValue)
                },
                analyticsTracker = appContainer.analyticsTracker,
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
                onEditDiaryEntry = { entryId -> navController.navigate(CareTailRoute.EditDiaryEntry.createRoute(entryId)) },
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
                reviewPromptManager = appContainer.reviewPromptManager,
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
                onOpenPremium = { reason ->
                    openPremium(reason, currentScreen, reason.routeValue)
                },
                analyticsTracker = appContainer.analyticsTracker,
            )
        }
        composable(
            route = CareTailRoute.EditDiaryEntry.route,
            arguments = listOf(navArgument(CareTailRoute.EditDiaryEntry.entryIdArg) { type = NavType.LongType }),
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong(CareTailRoute.EditDiaryEntry.entryIdArg) ?: 0L
            AddDiaryEntryScreen(
                currentRoute = currentRoute,
                onNavigate = onBottomNavigate,
                petRepository = appContainer.petRepository,
                healthDiaryRepository = appContainer.healthDiaryRepository,
                reviewPromptManager = appContainer.reviewPromptManager,
                preselectedPetId = null,
                editEntryId = entryId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.navigate(CareTailRoute.Diary.route) { launchSingleTop = true } },
                onAddPet = { navController.navigate(CareTailRoute.AddPet.route) },
                onOpenPremium = { reason ->
                    openPremium(reason, currentScreen, reason.routeValue)
                },
                analyticsTracker = appContainer.analyticsTracker,
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
                onEditDocument = { documentId -> navController.navigate(CareTailRoute.EditDocument.createRoute(documentId)) },
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
                onOpenPremium = { reason ->
                    openPremium(reason, currentScreen, reason.routeValue)
                },
                analyticsTracker = appContainer.analyticsTracker,
            )
        }
        composable(
            route = CareTailRoute.EditDocument.route,
            arguments = listOf(navArgument(CareTailRoute.EditDocument.documentIdArg) { type = NavType.LongType }),
        ) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getLong(CareTailRoute.EditDocument.documentIdArg) ?: 0L
            AddDocumentScreen(
                currentRoute = currentRoute,
                onNavigate = onBottomNavigate,
                petRepository = appContainer.petRepository,
                petDocumentRepository = appContainer.petDocumentRepository,
                preselectedPetId = null,
                editDocumentId = documentId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.navigate(CareTailRoute.Documents.route) { launchSingleTop = true } },
                onAddPet = { navController.navigate(CareTailRoute.AddPet.route) },
                onOpenPremium = { reason ->
                    openPremium(reason, currentScreen, reason.routeValue)
                },
                analyticsTracker = appContainer.analyticsTracker,
            )
        }
        composable(
            route = CareTailRoute.Premium.route,
            arguments = listOf(
                navArgument(CareTailRoute.Premium.reasonArg) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) { backStackEntry ->
            val reason = PremiumUpsellReason.fromRouteValue(
                backStackEntry.arguments?.getString(CareTailRoute.Premium.reasonArg),
            )
            PremiumScreen(
                reason = reason,
                billingRepository = appContainer.billingRepository,
                analyticsTracker = appContainer.analyticsTracker,
                onClose = closePremium,
                onMaybeLater = closePremium,
            )
        }
        composable(CareTailRoute.Settings.route) {
            SettingsScreen(
                currentRoute = currentRoute,
                onNavigate = onBottomNavigate,
                onOpenPremium = { reason ->
                    openPremium(reason, AnalyticsTracker.Screens.Settings, reason?.routeValue ?: AnalyticsTracker.SourceSettings)
                },
                onOpenDocuments = { navController.navigate(CareTailRoute.Documents.route) },
                petRepository = appContainer.petRepository,
                reminderRepository = appContainer.reminderRepository,
                healthDiaryRepository = appContainer.healthDiaryRepository,
                petDocumentRepository = appContainer.petDocumentRepository,
                reminderNotificationScheduler = appContainer.reminderNotificationScheduler,
                notificationPreferences = appContainer.notificationPreferences,
                billingRepository = appContainer.billingRepository,
                authUiState = authUiState,
                onGoogleSignIn = authViewModel::signInWithGoogle,
                onSignOut = authViewModel::signOut,
                onDeleteAccount = authViewModel::deleteAccount,
                onClearAuthError = authViewModel::clearError,
                onLocalDataDeleted = {
                    navController.navigate(CareTailRoute.Home.route) {
                        popUpTo(CareTailRoute.Home.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}

private fun String.analyticsScreenName(): String? = when {
    this == CareTailRoute.Onboarding.route -> AnalyticsTracker.Screens.Onboarding
    this == CareTailRoute.Home.route -> AnalyticsTracker.Screens.Home
    this == CareTailRoute.Pets.route -> AnalyticsTracker.Screens.Pets
    this == CareTailRoute.PetProfile.route -> AnalyticsTracker.Screens.PetProfile
    this == CareTailRoute.AddPet.route -> AnalyticsTracker.Screens.AddPet
    this == CareTailRoute.EditPet.route -> AnalyticsTracker.Screens.EditPet
    this == CareTailRoute.Reminders.route -> AnalyticsTracker.Screens.Reminders
    this == CareTailRoute.AddReminder.route -> AnalyticsTracker.Screens.AddReminder
    this == CareTailRoute.EditReminder.route -> AnalyticsTracker.Screens.EditReminder
    this == CareTailRoute.Diary.route -> AnalyticsTracker.Screens.Diary
    this == CareTailRoute.AddDiaryEntry.route -> AnalyticsTracker.Screens.AddDiaryEntry
    this == CareTailRoute.EditDiaryEntry.route -> AnalyticsTracker.Screens.EditDiaryEntry
    this == CareTailRoute.Documents.route -> AnalyticsTracker.Screens.Documents
    this == CareTailRoute.AddDocument.route -> AnalyticsTracker.Screens.AddDocument
    this == CareTailRoute.EditDocument.route -> AnalyticsTracker.Screens.EditDocument
    this == CareTailRoute.Premium.route -> AnalyticsTracker.Screens.Premium
    this == CareTailRoute.Settings.route -> AnalyticsTracker.Screens.Settings
    else -> null
}
