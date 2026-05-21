package com.caretail.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caretail.app.billing.PremiumManager
import com.caretail.app.billing.PremiumLimits
import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.data.repository.PetRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class PetsUiState(
    val pets: List<PetEntity> = emptyList(),
    val canAddPet: Boolean = true,
    val isPremium: Boolean = false,
    val freePetLimit: Int = PremiumLimits.FREE_PET_LIMIT,
)

class PetsViewModel(
    petRepository: PetRepository,
) : ViewModel() {
    val uiState: StateFlow<PetsUiState> = combine(
        petRepository.observeAllPets(),
        PremiumManager.isPremium,
    ) { pets, isPremium ->
            PetsUiState(
                pets = pets,
                canAddPet = PremiumManager.canAddPet(pets.size),
                isPremium = isPremium,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PetsUiState(),
        )
}
