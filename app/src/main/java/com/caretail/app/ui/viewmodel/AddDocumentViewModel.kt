package com.caretail.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caretail.app.data.local.entities.PetDocumentEntity
import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.data.repository.PetDocumentRepository
import com.caretail.app.data.repository.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val DocumentTypeValues = listOf("Vaccine Record", "Lab Result", "Prescription", "Insurance", "Other")

data class AddDocumentUiState(
    val pets: List<PetEntity> = emptyList(),
    val selectedPetId: Long? = null,
    val title: String = "",
    val type: String = DocumentTypeValues.first(),
    val fileUri: String? = null,
    val fileName: String? = null,
    val notes: String = "",
    val isLoading: Boolean = true,
    val success: Boolean = false,
    val validationError: String? = null,
    val generalError: String? = null,
)

class AddDocumentViewModel(
    private val petRepository: PetRepository,
    private val petDocumentRepository: PetDocumentRepository,
    private val preselectedPetId: Long? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddDocumentUiState())
    val uiState: StateFlow<AddDocumentUiState> = _uiState.asStateFlow()

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
    }

    fun onPetSelected(petId: Long) = update { copy(selectedPetId = petId, validationError = null, generalError = null) }

    fun onTitleChanged(value: String) = update { copy(title = value, validationError = null, generalError = null) }

    fun onTypeSelected(value: String) = update { copy(type = value, validationError = null, generalError = null) }

    fun onFileSelected(uri: String, displayName: String) = update {
        copy(fileUri = uri, fileName = displayName, validationError = null, generalError = null)
    }

    fun onNotesChanged(value: String) = update { copy(notes = value) }

    fun saveDocument() {
        val state = uiState.value
        val petId = state.selectedPetId
        when {
            state.pets.isEmpty() -> {
                update { copy(validationError = "Add a pet before adding records.") }
                return
            }
            petId == null -> {
                update { copy(validationError = "Choose a pet.") }
                return
            }
            state.title.trim().isBlank() -> {
                update { copy(validationError = "Document title is required.") }
                return
            }
            state.type !in DocumentTypeValues -> {
                update { copy(validationError = "Choose a document type.") }
                return
            }
        }

        viewModelScope.launch {
            update { copy(isLoading = true, validationError = null, generalError = null) }
            try {
                val now = System.currentTimeMillis()
                petDocumentRepository.addDocument(
                    PetDocumentEntity(
                        petId = petId,
                        title = state.title.trim(),
                        type = state.type,
                        fileUri = state.fileUri,
                        notes = state.notes.trim().ifBlank { null },
                        createdAtMillis = now,
                        updatedAtMillis = now,
                    ),
                )
                update { copy(isLoading = false, success = true) }
            } catch (error: Exception) {
                update { copy(isLoading = false, generalError = error.message ?: "Unable to save document.") }
            }
        }
    }

    private fun update(block: AddDocumentUiState.() -> AddDocumentUiState) {
        _uiState.update(block)
    }
}
