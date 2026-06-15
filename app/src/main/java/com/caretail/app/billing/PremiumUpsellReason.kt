package com.caretail.app.billing

enum class PremiumUpsellReason(
    val routeValue: String,
    val title: String,
    val message: String,
    val benefits: List<String>,
    val callToAction: String,
) {
    PetLimit(
        routeValue = "pet_limit",
        title = "Add every pet in your household",
        message = "Free includes 1 pet profile. Premium keeps separate profiles, reminders, and care history for every pet.",
        benefits = listOf("Unlimited pet profiles", "Separate routines and records", "Multi-pet household organization"),
        callToAction = "Unlock Unlimited Pets",
    ),
    ReminderLimit(
        routeValue = "reminder_limit",
        title = "You have reached the free reminder limit",
        message = "Free includes 5 active reminders. Premium lets you keep adding medication, grooming, vaccine, vet visit, and care routine reminders.",
        benefits = listOf("Unlimited active reminders", "More care categories", "Monthly and yearly repeat schedules"),
        callToAction = "Get Unlimited Reminders",
    ),
    ExportLocked(
        routeValue = "export_locked",
        title = "Create a care report for your vet",
        message = "Turn medications, vaccines, visits, symptoms, notes, and records into a clear report you can share before an appointment.",
        benefits = listOf("Shareable care report", "Medication and vaccine summary", "Symptoms, notes, and visit history"),
        callToAction = "Unlock Care Reports",
    ),
    AdvancedRepeatLocked(
        routeValue = "advanced_repeat_locked",
        title = "Plan long-term care routines",
        message = "Monthly and yearly reminders help you stay ahead of vaccines, preventives, grooming, and recurring vet care.",
        benefits = listOf("Monthly reminder schedules", "Yearly reminder schedules", "Less manual routine setup"),
        callToAction = "Unlock Advanced Reminders",
    ),
    DiaryLimit(
        routeValue = "diary_limit",
        title = "Keep building your pet's care history",
        message = "Free includes 5 health diary entries. Premium unlocks unlimited health notes for ongoing care tracking.",
        benefits = listOf("Unlimited health diary entries", "Symptoms, appetite, mood, and energy history", "Care history for every pet"),
        callToAction = "Unlock Unlimited Notes",
    ),
    DocumentLimit(
        routeValue = "document_limit",
        title = "Organize more pet records",
        message = "Free includes 3 document records. Premium unlocks unlimited records for vaccines, prescriptions, insurance, and vet visits.",
        benefits = listOf("Unlimited document records", "Organized care history", "Records ready when you need them"),
        callToAction = "Unlock Unlimited Records",
    );

    companion object {
        fun fromRouteValue(value: String?): PremiumUpsellReason? =
            entries.firstOrNull { it.routeValue == value }
    }
}
