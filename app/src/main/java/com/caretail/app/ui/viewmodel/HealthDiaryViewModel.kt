package com.caretail.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caretail.app.billing.PremiumLimits
import com.caretail.app.billing.PremiumManager
import com.caretail.app.data.repository.HealthDiaryRepository
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.ui.model.HealthDiaryEntryUiModel
import com.caretail.app.ui.model.mapHealthDiaryEntryUiModels
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HealthDiaryUiState(
    val pets: List<com.caretail.app.data.local.entities.PetEntity> = emptyList(),
    val selectedPetId: Long? = null,
    val groupedEntries: Map<String, List<HealthDiaryEntryUiModel>> = emptyMap(),
    val totalEntryCount: Int = 0,
    val isPremium: Boolean = false,
    val freeEntryLimit: Int = PremiumLimits.FREE_DIARY_ENTRY_LIMIT,
    val isLoading: Boolean = true,
) {
    val selectedPetName: String
        get() = pets.firstOrNull { it.id == selectedPetId }?.name.orEmpty()

    val hasEntries: Boolean
        get() = groupedEntries.values.any { it.isNotEmpty() }
}

@OptIn(ExperimentalCoroutinesApi::class)
class HealthDiaryViewModel(
    private val petRepository: PetRepository,
    private val healthDiaryRepository: HealthDiaryRepository,
) : ViewModel() {
    private val selectedPetId = MutableStateFlow<Long?>(null)

    private val petsFlow = petRepository.observeAllPets()

    private val entriesFlow: Flow<List<com.caretail.app.data.local.entities.HealthDiaryEntryEntity>> =
        selectedPetId.flatMapLatest { petId ->
            petId?.let { healthDiaryRepository.observeEntriesForPet(it) } ?: flowOf(emptyList())
        }

    val uiState: StateFlow<HealthDiaryUiState> = combine(
        petsFlow,
        selectedPetId,
        entriesFlow,
        healthDiaryRepository.observeAllEntries(),
        PremiumManager.isPremium,
    ) { pets, selectedId, entries, allEntries, isPremium ->
        val resolvedPetId = when {
            selectedId != null && pets.any { it.id == selectedId } -> selectedId
            else -> pets.firstOrNull()?.id
        }
        if (resolvedPetId != selectedId) {
            selectedPetId.value = resolvedPetId
        }
        val models = mapHealthDiaryEntryUiModels(entries, pets)
        HealthDiaryUiState(
            pets = pets,
            selectedPetId = resolvedPetId,
            groupedEntries = models.groupBy { it.dateLabel },
            totalEntryCount = allEntries.size,
            isPremium = isPremium,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HealthDiaryUiState(),
    )

    fun onPetSelected(petId: Long) {
        selectedPetId.update { petId }
    }

    fun deleteEntry(entry: HealthDiaryEntryUiModel) {
        viewModelScope.launch {
            healthDiaryRepository.getEntryById(entry.id)?.let { healthDiaryRepository.deleteEntry(it) }
        }
    }
}
