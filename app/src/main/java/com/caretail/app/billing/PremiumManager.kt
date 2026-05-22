package com.caretail.app.billing

import com.caretail.app.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PremiumManager {
    private val _hasBillingEntitlement = MutableStateFlow(false)
    private val _isPremiumTestMode = MutableStateFlow(false)
    private val _isPremium = MutableStateFlow(false)

    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()
    val isPremiumTestMode: StateFlow<Boolean> = _isPremiumTestMode.asStateFlow()

    fun setPremiumForTesting(enabled: Boolean) {
        if (BuildConfig.DEBUG) {
            _isPremiumTestMode.value = enabled
            updatePremiumState()
        }
    }

    fun setBillingEntitlement(active: Boolean) {
        _hasBillingEntitlement.value = active
        updatePremiumState()
    }

    private fun updatePremiumState() {
        _isPremium.value = _hasBillingEntitlement.value || (BuildConfig.DEBUG && _isPremiumTestMode.value)
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
