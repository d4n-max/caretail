package com.caretail.app.reminders

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val NotificationPreferencesName = "caretail_notification_preferences"
private const val CareRemindersEnabledKey = "care_reminders_enabled"

class NotificationPreferences(context: Context) {
    private val sharedPreferences = context.applicationContext.getSharedPreferences(
        NotificationPreferencesName,
        Context.MODE_PRIVATE,
    )
    private val _careRemindersEnabled = MutableStateFlow(
        sharedPreferences.getBoolean(CareRemindersEnabledKey, true),
    )

    val careRemindersEnabled: StateFlow<Boolean> = _careRemindersEnabled.asStateFlow()

    fun areCareRemindersEnabled(): Boolean = _careRemindersEnabled.value

    fun setCareRemindersEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(CareRemindersEnabledKey, enabled).apply()
        _careRemindersEnabled.value = enabled
    }
}
