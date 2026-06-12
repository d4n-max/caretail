package com.caretail.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caretail.app.billing.PremiumLimits
import com.caretail.app.billing.PremiumManager
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.data.repository.ReminderRepository
import com.caretail.app.reminders.ReminderNotificationScheduler
import com.caretail.app.review.ReviewPromptManager
import com.caretail.app.review.ReviewTrigger
import com.caretail.app.ui.model.ReminderUiModel
import com.caretail.app.ui.model.mapReminderUiModels
import com.caretail.app.util.isToday
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    val isPremium: Boolean = false,
    val hasPetProfile: Boolean = false,
    val activeReminderCount: Int = 0,
    val freeActiveReminderLimit: Int = PremiumLimits.FREE_ACTIVE_REMINDER_LIMIT,
    val generalError: String? = null,
) {
    val hasAnyReminder: Boolean
        get() = overdue.isNotEmpty() || today.isNotEmpty() || upcoming.isNotEmpty() || completed.isNotEmpty()
}

class RemindersViewModel(
    private val reminderRepository: ReminderRepository,
    private val reminderNotificationScheduler: ReminderNotificationScheduler,
    petRepository: PetRepository,
    private val reviewPromptManager: ReviewPromptManager,
) : ViewModel() {
    private val _reviewTriggerEvents = MutableSharedFlow<ReviewTrigger>()
    val reviewTriggerEvents: SharedFlow<ReviewTrigger> = _reviewTriggerEvents.asSharedFlow()

    val uiState: StateFlow<RemindersUiState> = combine(
        reminderRepository.observeAllReminders(),
        petRepository.observeAllPets(),
        PremiumManager.isPremium,
    ) { reminders, pets, isPremium ->
        val now = System.currentTimeMillis()
        val models = mapReminderUiModels(reminders, pets, now)
        val activeReminderCount = models.count { !it.isCompleted }
        RemindersUiState(
            overdue = models.filter { it.isOverdue }.sortedBy { it.dueAtMillis },
            today = models.filter { !it.isCompleted && !it.isOverdue && isToday(it.dueAtMillis, now) }.sortedBy { it.dueAtMillis },
            upcoming = models.filter { !it.isCompleted && !it.isOverdue && !isToday(it.dueAtMillis, now) }.sortedBy { it.dueAtMillis },
            completed = models.filter { it.isCompleted }.sortedByDescending { it.dueAtMillis },
            isLoading = false,
            isPremium = isPremium,
            hasPetProfile = pets.isNotEmpty(),
            activeReminderCount = activeReminderCount,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RemindersUiState(),
    )

    fun markCompleted(reminderId: Long) {
        viewModelScope.launch {
            reminderRepository.markReminderCompleted(reminderId, System.currentTimeMillis())
            reminderNotificationScheduler.cancelReminder(reminderId)
            reviewPromptManager.onReminderCompleted()
            _reviewTriggerEvents.emit(ReviewTrigger.ReminderCompleted)
        }
    }

    fun markIncomplete(reminder: ReminderUiModel) {
        viewModelScope.launch {
            reminderRepository.markReminderIncomplete(reminder.id, System.currentTimeMillis())
            reminderRepository.getReminderById(reminder.id)?.let { entity ->
                if (entity.dueAtMillis > System.currentTimeMillis()) {
                    reminderNotificationScheduler.rescheduleReminder(entity, reminder.petName)
                }
            }
        }
    }

    fun deleteReminder(reminder: ReminderUiModel) {
        viewModelScope.launch {
            reminderNotificationScheduler.cancelReminder(reminder.id)
            reminderRepository.getReminderById(reminder.id)?.let { reminderRepository.deleteReminder(it) }
        }
    }
}
