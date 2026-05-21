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
    data object EditPet : CareTailRoute("edit_pet/{petId}", "Edit Pet") {
        const val petIdArg = "petId"
        fun createRoute(petId: Long): String = "edit_pet/$petId"
    }
    data object Reminders : CareTailRoute("reminders", "Reminders")
    data object AddReminder : CareTailRoute("add_reminder?petId={petId}", "Add Reminder") {
        const val petIdArg = "petId"
        fun createRoute(petId: Long? = null): String = petId?.let { "add_reminder?petId=$it" } ?: "add_reminder"
    }
    data object EditReminder : CareTailRoute("edit_reminder/{reminderId}", "Edit Reminder") {
        const val reminderIdArg = "reminderId"
        fun createRoute(reminderId: Long): String = "edit_reminder/$reminderId"
    }
    data object Diary : CareTailRoute("diary", "Diary")
    data object AddDiaryEntry : CareTailRoute("add_diary_entry?petId={petId}", "Add Diary Entry") {
        const val petIdArg = "petId"
        fun createRoute(petId: Long? = null): String = petId?.let { "add_diary_entry?petId=$it" } ?: "add_diary_entry"
    }
    data object EditDiaryEntry : CareTailRoute("edit_diary_entry/{entryId}", "Edit Diary Entry") {
        const val entryIdArg = "entryId"
        fun createRoute(entryId: Long): String = "edit_diary_entry/$entryId"
    }
    data object Documents : CareTailRoute("documents", "Documents")
    data object AddDocument : CareTailRoute("add_document?petId={petId}", "Add Document") {
        const val petIdArg = "petId"
        fun createRoute(petId: Long? = null): String = petId?.let { "add_document?petId=$it" } ?: "add_document"
    }
    data object EditDocument : CareTailRoute("edit_document/{documentId}", "Edit Document") {
        const val documentIdArg = "documentId"
        fun createRoute(documentId: Long): String = "edit_document/$documentId"
    }
    data object Premium : CareTailRoute("premium?reason={reason}", "Premium") {
        const val reasonArg = "reason"
        fun createRoute(reason: com.caretail.app.billing.PremiumUpsellReason? = null): String =
            reason?.let { "premium?reason=${it.routeValue}" } ?: "premium"
    }
    data object Settings : CareTailRoute("settings", "Settings")
}

val MainBottomNavRoutes = listOf(
    CareTailRoute.Home,
    CareTailRoute.Pets,
    CareTailRoute.Reminders,
    CareTailRoute.Diary,
    CareTailRoute.Settings,
)
