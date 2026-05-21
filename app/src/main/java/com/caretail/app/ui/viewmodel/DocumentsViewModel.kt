package com.caretail.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caretail.app.billing.PremiumLimits
import com.caretail.app.billing.PremiumManager
import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.data.repository.PetDocumentRepository
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.ui.model.PetDocumentUiModel
import com.caretail.app.ui.model.mapPetDocumentUiModels
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DocumentsUiState(
    val pets: List<PetEntity> = emptyList(),
    val selectedPetId: Long? = null,
    val documents: List<PetDocumentUiModel> = emptyList(),
    val totalDocumentCount: Int = 0,
    val isPremium: Boolean = false,
    val freeDocumentLimit: Int = PremiumLimits.FREE_DOCUMENT_LIMIT,
    val isLoading: Boolean = true,
) {
    val hasDocuments: Boolean
        get() = documents.isNotEmpty()
}

class DocumentsViewModel(
    private val petRepository: PetRepository,
    private val petDocumentRepository: PetDocumentRepository,
) : ViewModel() {
    private val selectedPetId = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<DocumentsUiState> = combine(
        petRepository.observeAllPets(),
        petDocumentRepository.observeAllDocuments(),
        selectedPetId,
        PremiumManager.isPremium,
    ) { pets, documents, selectedId, isPremium ->
        val resolvedPetId = when {
            selectedId != null && pets.any { it.id == selectedId } -> selectedId
            else -> pets.firstOrNull()?.id
        }
        if (resolvedPetId != selectedId) {
            selectedPetId.value = resolvedPetId
        }
        DocumentsUiState(
            pets = pets,
            selectedPetId = resolvedPetId,
            documents = mapPetDocumentUiModels(
                documents.filter { document -> resolvedPetId == null || document.petId == resolvedPetId },
                pets,
            ),
            totalDocumentCount = documents.size,
            isPremium = isPremium,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DocumentsUiState(),
    )

    fun onPetSelected(petId: Long) {
        selectedPetId.update { petId }
    }

    fun deleteDocument(document: PetDocumentUiModel) {
        viewModelScope.launch {
            petDocumentRepository.getDocumentById(document.id)?.let { petDocumentRepository.deleteDocument(it) }
        }
    }
}
