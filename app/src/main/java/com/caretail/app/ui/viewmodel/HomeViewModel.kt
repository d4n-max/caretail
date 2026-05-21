package com.caretail.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caretail.app.billing.PremiumManager
import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.data.repository.ReminderRepository
import com.caretail.app.ui.model.ReminderUiModel
import com.caretail.app.ui.model.mapReminderUiModels
import com.caretail.app.util.isToday
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val pets: List<PetEntity> = emptyList(),
    val todayReminders: List<ReminderUiModel> = emptyList(),
    val upcomingReminders: List<ReminderUiModel> = emptyList(),
    val canAddPet: Boolean = true,
)

class HomeViewModel(
    petRepository: PetRepository,
    reminderRepository: ReminderRepository,
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = combine(
        petRepository.observeAllPets(),
        reminderRepository.observeAllReminders(),
    ) { pets, reminders ->
            val now = System.currentTimeMillis()
            val activeReminders = mapReminderUiModels(reminders, pets, now)
                .filter { !it.isCompleted && !it.isOverdue }
            HomeUiState(
                pets = pets,
                todayReminders = activeReminders
                    .filter { isToday(it.dueAtMillis, now) }
                    .sortedBy { it.dueAtMillis }
                    .take(3),
                upcomingReminders = activeReminders
                    .filter { !isToday(it.dueAtMillis, now) }
                    .sortedBy { it.dueAtMillis }
                    .take(3),
                canAddPet = PremiumManager.canAddPet(pets.size),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )
}
