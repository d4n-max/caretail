package com.caretail.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caretail.app.billing.PremiumManager
import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.data.repository.HealthDiaryRepository
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.data.repository.ReminderRepository
import com.caretail.app.ui.model.HealthDiaryEntryUiModel
import com.caretail.app.ui.model.ReminderUiModel
import com.caretail.app.ui.model.mapHealthDiaryEntryUiModels
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
    val latestDiaryEntry: HealthDiaryEntryUiModel? = null,
    val canAddPet: Boolean = true,
    val isPremium: Boolean = false,
)

class HomeViewModel(
    petRepository: PetRepository,
    reminderRepository: ReminderRepository,
    healthDiaryRepository: HealthDiaryRepository,
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = combine(
        petRepository.observeAllPets(),
        reminderRepository.observeAllReminders(),
        healthDiaryRepository.observeAllEntries(),
        PremiumManager.isPremium,
    ) { pets, reminders, diaryEntries, isPremium ->
            val now = System.currentTimeMillis()
            val activeReminders = mapReminderUiModels(reminders, pets, now)
                .filter { !it.isCompleted && !it.isOverdue }
            val latestDiaryEntry = mapHealthDiaryEntryUiModels(diaryEntries, pets, now)
                .maxByOrNull { it.entryDateMillis }
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
                latestDiaryEntry = latestDiaryEntry,
                canAddPet = PremiumManager.canAddPet(pets.size),
                isPremium = isPremium,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )
}
