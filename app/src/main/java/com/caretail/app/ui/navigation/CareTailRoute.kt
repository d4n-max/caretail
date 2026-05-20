package com.caretail.app.ui.navigation

sealed class CareTailRoute(val route: String, val label: String) {
    data object Onboarding : CareTailRoute("onboarding", "Onboarding")
    data object Home : CareTailRoute("home", "Home")
    data object Pets : CareTailRoute("pets", "Pets")
    data object PetProfile : CareTailRoute("pet_profile/{petId}", "Pet Profile") {
        const val petIdArg = "petId"
        fun createRoute(petId: Long): String = "pet_profile/$petId"
    }
    data object AddPet : CareTailRoute("add_pet", "Add Pet")
    data object Reminders : CareTailRoute("reminders", "Reminders")
    data object AddReminder : CareTailRoute("add_reminder", "Add Reminder")
    data object Diary : CareTailRoute("diary", "Diary")
    data object AddDiaryEntry : CareTailRoute("add_diary_entry", "Add Diary Entry")
    data object Documents : CareTailRoute("documents", "Documents")
    data object Premium : CareTailRoute("premium", "Premium")
    data object Settings : CareTailRoute("settings", "Settings")
}

val MainBottomNavRoutes = listOf(
    CareTailRoute.Home,
    CareTailRoute.Pets,
    CareTailRoute.Reminders,
    CareTailRoute.Diary,
    CareTailRoute.Settings,
)
