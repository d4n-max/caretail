package com.caretail.app.billing

enum class PremiumUpsellReason(
    val routeValue: String,
    val title: String,
    val message: String,
) {
    PetLimit(
        routeValue = "pet_limit",
        title = "You reached the free pet limit.",
        message = "Free plan includes 1 pet. Upgrade to Premium for unlimited pets.",
    ),
    ReminderLimit(
        routeValue = "reminder_limit",
        title = "You reached the free reminder limit.",
        message = "Free plan includes 5 active reminders. Upgrade for unlimited care reminders.",
    ),
    DiaryLimit(
        routeValue = "diary_limit",
        title = "You reached the free health notes limit.",
        message = "Free plan includes 5 health notes. Upgrade for unlimited health diary.",
    ),
    DocumentLimit(
        routeValue = "document_limit",
        title = "You reached the free document limit.",
        message = "Free plan includes 3 document records. Upgrade for unlimited records.",
    ),
    ExportLocked(
        routeValue = "export_locked",
        title = "Exportable care reports are Premium.",
        message = "Exportable care reports are included with CareTail Premium.",
    ),
    AdvancedRepeatLocked(
        routeValue = "advanced_repeat_locked",
        title = "Advanced repeats are Premium.",
        message = "Monthly and yearly reminders are included with Premium.",
    ),
    CloudBackupLocked(
        routeValue = "cloud_backup_locked",
        title = "Cloud backup is Premium.",
        message = "Future cloud backup will be included with Premium.",
    );

    companion object {
        fun fromRouteValue(value: String?): PremiumUpsellReason? =
            entries.firstOrNull { it.routeValue == value }
    }
}
