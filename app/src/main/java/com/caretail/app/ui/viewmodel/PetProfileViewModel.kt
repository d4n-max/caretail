package com.caretail.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.data.repository.ReminderRepository
import com.caretail.app.ui.model.ReminderUiModel
import com.caretail.app.ui.model.mapReminderUiModels
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class PetProfileUiState(
    val pet: PetEntity? = null,
    val upcomingReminders: List<ReminderUiModel> = emptyList(),
    val isLoading: Boolean = true,
)

class PetProfileViewModel(
    petRepository: PetRepository,
    reminderRepository: ReminderRepository,
    petId: Long,
) : ViewModel() {
    val uiState: StateFlow<PetProfileUiState> = combine(
        petRepository.observePetById(petId),
        reminderRepository.observeRemindersForPet(petId),
    ) { pet, reminders ->
        val now = System.currentTimeMillis()
        PetProfileUiState(
            pet = pet,
            upcomingReminders = mapReminderUiModels(reminders, pet?.let(::listOf) ?: emptyList(), now)
                .filter { !it.isCompleted && !it.isOverdue }
                .sortedBy { it.dueAtMillis }
                .take(3),
            isLoading = false,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PetProfileUiState(),
        )
}
