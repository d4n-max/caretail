package com.caretail.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.data.repository.ReminderRepository
import com.caretail.app.ui.model.ReminderUiModel
import com.caretail.app.ui.model.mapReminderUiModels
import com.caretail.app.util.isToday
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RemindersUiState(
    val overdue: List<ReminderUiModel> = emptyList(),
    val today: List<ReminderUiModel> = emptyList(),
    val upcoming: List<ReminderUiModel> = emptyList(),
    val completed: List<ReminderUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val generalError: String? = null,
) {
    val hasAnyReminder: Boolean
        get() = overdue.isNotEmpty() || today.isNotEmpty() || upcoming.isNotEmpty() || completed.isNotEmpty()
}

class RemindersViewModel(
    private val reminderRepository: ReminderRepository,
    petRepository: PetRepository,
) : ViewModel() {
    val uiState: StateFlow<RemindersUiState> = combine(
        reminderRepository.observeAllReminders(),
        petRepository.observeAllPets(),
    ) { reminders, pets ->
        val now = System.currentTimeMillis()
        val models = mapReminderUiModels(reminders, pets, now)
        RemindersUiState(
            overdue = models.filter { it.isOverdue }.sortedBy { it.dueAtMillis },
            today = models.filter { !it.isCompleted && !it.isOverdue && isToday(it.dueAtMillis, now) }.sortedBy { it.dueAtMillis },
            upcoming = models.filter { !it.isCompleted && !it.isOverdue && !isToday(it.dueAtMillis, now) }.sortedBy { it.dueAtMillis },
            completed = models.filter { it.isCompleted }.sortedByDescending { it.dueAtMillis },
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RemindersUiState(),
    )

    fun markCompleted(reminderId: Long) {
        viewModelScope.launch {
            reminderRepository.markReminderCompleted(reminderId, System.currentTimeMillis())
        }
    }

    fun markIncomplete(reminderId: Long) {
        viewModelScope.launch {
            reminderRepository.markReminderIncomplete(reminderId, System.currentTimeMillis())
        }
    }

    fun deleteReminder(reminder: ReminderUiModel) {
        viewModelScope.launch {
            reminderRepository.getReminderById(reminder.id)?.let { reminderRepository.deleteReminder(it) }
        }
    }
}
