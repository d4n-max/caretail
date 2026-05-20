package com.caretail.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.data.repository.PetRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class PetProfileUiState(
    val pet: PetEntity? = null,
    val isLoading: Boolean = true,
)

class PetProfileViewModel(
    petRepository: PetRepository,
    petId: Long,
) : ViewModel() {
    val uiState: StateFlow<PetProfileUiState> = petRepository.observePetById(petId)
        .map { pet -> PetProfileUiState(pet = pet, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PetProfileUiState(),
        )
}
