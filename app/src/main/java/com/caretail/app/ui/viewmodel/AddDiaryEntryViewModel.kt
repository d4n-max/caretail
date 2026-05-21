package com.caretail.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caretail.app.billing.PremiumManager
import com.caretail.app.billing.PremiumUpsellReason
import com.caretail.app.data.local.entities.HealthDiaryEntryEntity
import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.data.repository.HealthDiaryRepository
import com.caretail.app.data.repository.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val MoodValues = listOf("Great", "Normal", "Low", "Unwell")
val AppetiteValues = listOf("Normal", "Increased", "Reduced", "None")
val EnergyLevelValues = listOf("High", "Normal", "Low")

data class AddDiaryEntryUiState(
    val pets: List<PetEntity> = emptyList(),
    val editingEntryId: Long? = null,
    val createdAtMillis: Long? = null,
    val selectedPetId: Long? = null,
    val entryDateMillis: Long = System.currentTimeMillis(),
    val mood: String = "Normal",
    val appetite: String = "Normal",
    val energyLevel: String = "Normal",
    val symptoms: String = "",
    val notes: String = "",
    val imageUri: String? = null,
    val isLoading: Boolean = true,
    val success: Boolean = false,
    val validationError: String? = null,
    val generalError: String? = null,
    val upsellReason: PremiumUpsellReason? = null,
)

class AddDiaryEntryViewModel(
    private val petRepository: PetRepository,
    private val healthDiaryRepository: HealthDiaryRepository,
    private val preselectedPetId: Long? = null,
    private val editEntryId: Long? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddDiaryEntryUiState())
    val uiState: StateFlow<AddDiaryEntryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            petRepository.observeAllPets().collect { pets ->
                _uiState.update { state ->
                    val selectedStillExists = state.selectedPetId?.let { id -> pets.any { it.id == id } } == true
                    val preselectedExists = preselectedPetId?.let { id -> pets.any { it.id == id } } == true
                    state.copy(
                        pets = pets,
                        selectedPetId = when {
                            selectedStillExists -> state.selectedPetId
                            preselectedExists -> preselectedPetId
                            else -> pets.firstOrNull()?.id
                        },
                        isLoading = false,
                    )
                }
            }
        }
        editEntryId?.let { entryId ->
            viewModelScope.launch {
                val entry = healthDiaryRepository.getEntryById(entryId)
                if (entry != null) {
                    _uiState.update {
                        it.copy(
                            editingEntryId = entry.id,
                            createdAtMillis = entry.createdAtMillis,
                            selectedPetId = entry.petId,
                            entryDateMillis = entry.entryDateMillis,
                            mood = entry.mood,
                            appetite = entry.appetite,
                            energyLevel = entry.energyLevel,
                            symptoms = entry.symptoms.orEmpty(),
                            notes = entry.notes.orEmpty(),
                            imageUri = entry.imageUri,
                            isLoading = false,
                        )
                    }
                }
            }
        }
    }

    fun onPetSelected(petId: Long) = update { copy(selectedPetId = petId, validationError = null, generalError = null) }

    fun onMoodSelected(value: String) = update { copy(mood = value, validationError = null, generalError = null) }

    fun onAppetiteSelected(value: String) = update { copy(appetite = value, validationError = null, generalError = null) }

    fun onEnergySelected(value: String) = update { copy(energyLevel = value, validationError = null, generalError = null) }

    fun onSymptomsChanged(value: String) = update { copy(symptoms = value) }

    fun onNotesChanged(value: String) = update { copy(notes = value) }

    fun saveEntry() {
        val state = uiState.value
        val petId = state.selectedPetId
        when {
            state.pets.isEmpty() -> {
                update { copy(validationError = "Add a pet before adding health notes.") }
                return
            }
            petId == null -> {
                update { copy(validationError = "Choose a pet.") }
                return
            }
            state.mood !in MoodValues -> {
                update { copy(validationError = "Choose a mood.") }
                return
            }
            state.appetite !in AppetiteValues -> {
                update { copy(validationError = "Choose an appetite.") }
                return
            }
            state.energyLevel !in EnergyLevelValues -> {
                update { copy(validationError = "Choose an energy level.") }
                return
            }
        }

        viewModelScope.launch {
            update { copy(isLoading = true, validationError = null, generalError = null) }
            try {
                val totalEntryCount = healthDiaryRepository.getEntryCount()
                if (state.editingEntryId == null && !PremiumManager.canAddDiaryEntry(totalEntryCount)) {
                    update { copy(isLoading = false, upsellReason = PremiumUpsellReason.DiaryLimit) }
                    return@launch
                }
                val now = System.currentTimeMillis()
                val entry = HealthDiaryEntryEntity(
                    id = state.editingEntryId ?: 0L,
                    petId = petId,
                    entryDateMillis = state.entryDateMillis,
                    mood = state.mood,
                    appetite = state.appetite,
                    energyLevel = state.energyLevel,
                    symptoms = state.symptoms.trim().ifBlank { null },
                    notes = state.notes.trim().ifBlank { null },
                    imageUri = state.imageUri,
                    createdAtMillis = state.createdAtMillis ?: now,
                    updatedAtMillis = now,
                )
                if (state.editingEntryId == null) {
                    healthDiaryRepository.addEntry(entry)
                } else {
                    healthDiaryRepository.updateEntry(entry)
                }
                update { copy(isLoading = false, success = true) }
            } catch (error: Exception) {
                update { copy(isLoading = false, generalError = error.message ?: "Unable to save health note.") }
            }
        }
    }

    private fun update(block: AddDiaryEntryUiState.() -> AddDiaryEntryUiState) {
        _uiState.update(block)
    }
}
