package com.caretail.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caretail.app.billing.PremiumManager
import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.data.repository.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddPetUiState(
    val name: String = "",
    val species: String = "Cat",
    val breed: String = "",
    val gender: String = "",
    val birthDateText: String = "",
    val weightKg: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
    val validationError: String? = null,
    val generalError: String? = null,
    val savedPetId: Long? = null,
    val showPremiumUpsell: Boolean = false,
)

class AddPetViewModel(
    private val petRepository: PetRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddPetUiState())
    val uiState: StateFlow<AddPetUiState> = _uiState.asStateFlow()

    fun onNameChanged(value: String) = update { copy(name = value, validationError = null, generalError = null) }

    fun onSpeciesSelected(value: String) = update { copy(species = value, validationError = null, generalError = null) }

    fun onBreedChanged(value: String) = update { copy(breed = value) }

    fun onGenderChanged(value: String) = update { copy(gender = value) }

    fun onBirthDateChanged(value: String) = update { copy(birthDateText = value) }

    fun onWeightChanged(value: String) = update { copy(weightKg = value, validationError = null, generalError = null) }

    fun onNotesChanged(value: String) = update { copy(notes = value) }

    fun clearPremiumUpsell() = update { copy(showPremiumUpsell = false) }

    fun savePet() {
        val state = uiState.value
        val trimmedName = state.name.trim()
        if (trimmedName.isBlank()) {
            update { copy(validationError = "Pet name is required.") }
            return
        }
        if (state.species.isBlank()) {
            update { copy(validationError = "Choose a species.") }
            return
        }
        val parsedWeight = state.weightKg.trim().takeIf { it.isNotBlank() }?.toDoubleOrNull()
        if (state.weightKg.isNotBlank() && parsedWeight == null) {
            update { copy(validationError = "Weight must be a number.") }
            return
        }

        viewModelScope.launch {
            update { copy(isLoading = true, validationError = null, generalError = null) }
            try {
                val petCount = petRepository.getPetCount()
                if (!PremiumManager.canAddPet(petCount)) {
                    update { copy(isLoading = false, showPremiumUpsell = true) }
                    return@launch
                }
                val now = System.currentTimeMillis()
                val petId = petRepository.addPet(
                    PetEntity(
                        name = trimmedName,
                        species = state.species,
                        breed = state.breed.trim().ifBlank { null },
                        gender = state.gender.trim().ifBlank { null },
                        birthDateMillis = null,
                        weightKg = parsedWeight,
                        photoUri = null,
                        notes = state.notes.trim().ifBlank { null },
                        createdAtMillis = now,
                        updatedAtMillis = now,
                    ),
                )
                update { copy(isLoading = false, savedPetId = petId) }
            } catch (error: Exception) {
                update { copy(isLoading = false, generalError = error.message ?: "Unable to save pet.") }
            }
        }
    }

    private fun update(block: AddPetUiState.() -> AddPetUiState) {
        _uiState.update(block)
    }
}
