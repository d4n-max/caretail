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

data class HomeUiState(
    val pets: List<PetEntity> = emptyList(),
    val canAddPet: Boolean = true,
)

class HomeViewModel(
    petRepository: PetRepository,
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = petRepository.observeAllPets()
        .map { pets ->
            HomeUiState(
                pets = pets,
                canAddPet = PremiumManager.canAddPet(pets.size),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )
}
