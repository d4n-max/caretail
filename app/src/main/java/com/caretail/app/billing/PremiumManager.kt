package com.caretail.app.billing

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PremiumManager {
    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    fun setPremiumForTesting(enabled: Boolean) {
        _isPremium.value = enabled
    }

    fun canAddPet(currentPetCount: Int): Boolean =
        _isPremium.value || currentPetCount < PremiumLimits.FREE_PET_LIMIT

    fun canAddReminder(activeReminderCount: Int): Boolean =
        _isPremium.value || activeReminderCount < PremiumLimits.FREE_ACTIVE_REMINDER_LIMIT

    fun canAddDiaryEntry(totalDiaryEntryCount: Int): Boolean =
        _isPremium.value || totalDiaryEntryCount < PremiumLimits.FREE_DIARY_ENTRY_LIMIT

    fun canAddDocument(totalDocumentCount: Int): Boolean =
        _isPremium.value || totalDocumentCount < PremiumLimits.FREE_DOCUMENT_LIMIT

    fun canUseAdvancedRecurringReminders(): Boolean = _isPremium.value

    fun canExportHealthReport(): Boolean = _isPremium.value
}
