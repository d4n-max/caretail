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
    data object AddReminder : CareTailRoute("add_reminder?petId={petId}", "Add Reminder") {
        const val petIdArg = "petId"
        fun createRoute(petId: Long? = null): String = petId?.let { "add_reminder?petId=$it" } ?: "add_reminder"
    }
    data object Diary : CareTailRoute("diary", "Diary")
    data object AddDiaryEntry : CareTailRoute("add_diary_entry?petId={petId}", "Add Diary Entry") {
        const val petIdArg = "petId"
        fun createRoute(petId: Long? = null): String = petId?.let { "add_diary_entry?petId=$it" } ?: "add_diary_entry"
    }
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
