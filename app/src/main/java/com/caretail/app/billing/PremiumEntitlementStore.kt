package com.caretail.app.billing

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private const val PremiumEntitlementStoreName = "caretail_premium_entitlement"

private val Context.premiumEntitlementDataStore by preferencesDataStore(name = PremiumEntitlementStoreName)

class PremiumEntitlementStore(context: Context) {
    private val appContext = context.applicationContext

    suspend fun readCachedEntitlement(): PremiumEntitlementSnapshot {
        val preferences = appContext.premiumEntitlementDataStore.data.first()
        return PremiumEntitlementSnapshot(
            hasActiveSubscription = preferences[HasActiveSubscriptionKey] ?: false,
            lastBillingCheckAtMillis = preferences[LastBillingCheckAtMillisKey] ?: 0L,
        )
    }

    suspend fun saveEntitlement(
        hasActiveSubscription: Boolean,
        checkedAtMillis: Long = System.currentTimeMillis(),
    ) {
        appContext.premiumEntitlementDataStore.edit { preferences ->
            preferences[HasActiveSubscriptionKey] = hasActiveSubscription
            preferences[LastBillingCheckAtMillisKey] = checkedAtMillis
        }
    }

    private companion object {
        val HasActiveSubscriptionKey = booleanPreferencesKey("has_active_subscription")
        val LastBillingCheckAtMillisKey = longPreferencesKey("last_billing_check_at_millis")
    }
}

data class PremiumEntitlementSnapshot(
    val hasActiveSubscription: Boolean,
    val lastBillingCheckAtMillis: Long,
)
