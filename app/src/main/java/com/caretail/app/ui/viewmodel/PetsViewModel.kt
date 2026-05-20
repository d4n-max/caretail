package com.caretail.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caretail.app.billing.PremiumManager
import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.data.repository.PetRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class PetsUiState(
    val pets: List<PetEntity> = emptyList(),
    val canAddPet: Boolean = true,
)

class PetsViewModel(
    petRepository: PetRepository,
) : ViewModel() {
    val uiState: StateFlow<PetsUiState> = petRepository.observeAllPets()
        .map { pets ->
            PetsUiState(
                pets = pets,
                canAddPet = PremiumManager.canAddPet(pets.size),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PetsUiState(),
        )
}
