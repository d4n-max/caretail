package com.caretail.app.billing

object PremiumManager {
    const val freePetLimit = 1
    val isPremium: Boolean = false

    fun canAddPet(currentPetCount: Int): Boolean = isPremium || currentPetCount < freePetLimit
}
