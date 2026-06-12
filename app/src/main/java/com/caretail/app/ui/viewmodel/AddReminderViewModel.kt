package com.caretail.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caretail.app.billing.PremiumManager
import com.caretail.app.billing.PremiumUpsellReason
import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.data.local.entities.ReminderEntity
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.data.repository.ReminderRepository
import com.caretail.app.reminders.ReminderNotificationScheduler
import com.caretail.app.review.ReviewPromptManager
import com.caretail.app.util.defaultReminderDueAtMillis
import com.caretail.app.util.formatInputDate
import com.caretail.app.util.formatInputTime
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val ReminderTypes = listOf("Vaccine", "Medication", "Vet Visit", "Grooming", "Food", "Other")
val RepeatTypes = listOf("None", "Daily", "Weekly", "Monthly", "Yearly")
val PremiumRepeatTypes = listOf("Monthly", "Yearly")

data class AddReminderUiState(
    val pets: List<PetEntity> = emptyList(),
    val editingReminderId: Long? = null,
    val createdAtMillis: Long? = null,
    val isCompleted: Boolean = false,
    val completedAtMillis: Long? = null,
    val selectedPetId: Long? = null,
    val title: String = "",
    val type: String = ReminderTypes.first(),
    val selectedDateMillis: Long = dateOnlyMillis(defaultReminderDueAtMillis()),
    val selectedHour: Int = hourOfDay(defaultReminderDueAtMillis()),
    val selectedMinute: Int = minuteOfHour(defaultReminderDueAtMillis()),
    val date: String = formatInputDate(combineDateTimeMillis(selectedDateMillis, selectedHour, selectedMinute)),
    val time: String = formatInputTime(combineDateTimeMillis(selectedDateMillis, selectedHour, selectedMinute)),
    val repeatType: String = RepeatTypes.first(),
    val notes: String = "",
    val isLoading: Boolean = true,
    val success: Boolean = false,
    val successMessage: String? = null,
    val validationError: String? = null,
    val generalError: String? = null,
    val upsellReason: PremiumUpsellReason? = null,
)

class AddReminderViewModel(
    private val petRepository: PetRepository,
    private val reminderRepository: ReminderRepository,
    private val reminderNotificationScheduler: ReminderNotificationScheduler,
    private val reviewPromptManager: ReviewPromptManager,
    private val preselectedPetId: Long? = null,
    private val editReminderId: Long? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddReminderUiState())
    val uiState: StateFlow<AddReminderUiState> = _uiState.asStateFlow()

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
        editReminderId?.let { reminderId ->
            viewModelScope.launch {
                val reminder = reminderRepository.getReminderById(reminderId)
                if (reminder != null) {
                    _uiState.update {
                        it.copy(
                            editingReminderId = reminder.id,
                            createdAtMillis = reminder.createdAtMillis,
                            isCompleted = reminder.isCompleted,
                            completedAtMillis = reminder.completedAtMillis,
                            selectedPetId = reminder.petId,
                            title = reminder.title,
                            type = reminder.type,
                            selectedDateMillis = dateOnlyMillis(reminder.dueAtMillis),
                            selectedHour = hourOfDay(reminder.dueAtMillis),
                            selectedMinute = minuteOfHour(reminder.dueAtMillis),
                            date = formatInputDate(reminder.dueAtMillis),
                            time = formatInputTime(reminder.dueAtMillis),
                            repeatType = reminder.repeatType,
                            notes = reminder.notes.orEmpty(),
                            isLoading = false,
                        )
                    }
                }
            }
        }
    }

    fun onPetSelected(petId: Long) = update { copy(selectedPetId = petId, validationError = null, generalError = null) }

    fun onTitleChanged(value: String) = update { copy(title = value, validationError = null, generalError = null) }

    fun onTypeSelected(value: String) = update { copy(type = value, validationError = null, generalError = null) }

    fun onDateSelected(year: Int, zeroBasedMonth: Int, dayOfMonth: Int) {
        val selectedDateMillis = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, zeroBasedMonth)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        update {
            copy(
                selectedDateMillis = selectedDateMillis,
                date = formatInputDate(selectedDateMillis),
                validationError = null,
                generalError = null,
            )
        }
    }

    fun onTimeSelected(hour: Int, minute: Int) {
        update {
            val dueAtMillis = combineDateTimeMillis(selectedDateMillis, hour, minute)
            copy(
                selectedHour = hour,
                selectedMinute = minute,
                time = formatInputTime(dueAtMillis),
                validationError = null,
                generalError = null,
            )
        }
    }

    fun onRepeatTypeSelected(value: String) {
        if (value in PremiumRepeatTypes && !PremiumManager.canUseAdvancedRecurringReminders()) {
            update {
                copy(
                    validationError = null,
                    generalError = null,
                    upsellReason = PremiumUpsellReason.AdvancedRepeatLocked,
                )
            }
            return
        }
        update { copy(repeatType = value, validationError = null, generalError = null, upsellReason = null) }
    }

    fun onNotesChanged(value: String) = update { copy(notes = value) }

    fun onPremiumNavigationConsumed() = update { copy(upsellReason = null) }

    fun validateBeforePermissionRequest(): Boolean {
        val state = uiState.value
        if (validate(state) == null) return false
        if (state.repeatType in PremiumRepeatTypes && !PremiumManager.canUseAdvancedRecurringReminders()) {
            update { copy(upsellReason = PremiumUpsellReason.AdvancedRepeatLocked) }
            return false
        }
        return true
    }

    fun saveReminder(notificationPermissionGranted: Boolean) {
        val state = uiState.value
        val validated = validate(state) ?: return

        viewModelScope.launch {
            update { copy(isLoading = true, validationError = null, generalError = null) }
            try {
                val activeReminderCount = reminderRepository.getActiveReminderCount()
                if (state.editingReminderId == null && !PremiumManager.canAddReminder(activeReminderCount)) {
                    update { copy(isLoading = false, upsellReason = PremiumUpsellReason.ReminderLimit) }
                    return@launch
                }
                if (state.repeatType in PremiumRepeatTypes && !PremiumManager.canUseAdvancedRecurringReminders()) {
                    update { copy(isLoading = false, upsellReason = PremiumUpsellReason.AdvancedRepeatLocked) }
                    return@launch
                }
                val now = System.currentTimeMillis()
                val reminder = ReminderEntity(
                    id = state.editingReminderId ?: 0L,
                    petId = validated.petId,
                    title = state.title.trim(),
                    type = state.type,
                    notes = state.notes.trim().ifBlank { null },
                    dueAtMillis = validated.dueAtMillis,
                    repeatType = state.repeatType,
                    isCompleted = state.isCompleted,
                    completedAtMillis = state.completedAtMillis,
                    createdAtMillis = state.createdAtMillis ?: now,
                    updatedAtMillis = now,
                )
                val savedReminder = if (state.editingReminderId == null) {
                    val reminderId = reminderRepository.addReminder(reminder)
                    reviewPromptManager.onReminderCreated()
                    reminder.copy(id = reminderId)
                } else {
                    reminderRepository.updateReminder(reminder)
                    reminder
                }
                val petName = state.pets.firstOrNull { it.id == validated.petId }?.name.orEmpty()
                if (!savedReminder.isCompleted && savedReminder.dueAtMillis > System.currentTimeMillis()) {
                    reminderNotificationScheduler.rescheduleReminder(savedReminder, petName)
                } else {
                    reminderNotificationScheduler.cancelReminder(savedReminder.id)
                }
                update {
                    copy(
                        isLoading = false,
                        success = true,
                        successMessage = if (notificationPermissionGranted) {
                            "Reminder saved."
                        } else {
                            "Reminder saved. Notifications are disabled."
                        },
                    )
                }
            } catch (error: Exception) {
                update { copy(isLoading = false, generalError = error.message ?: "Unable to save reminder.") }
            }
        }
    }

    private fun validate(state: AddReminderUiState): ValidReminderForm? {
        val petId = state.selectedPetId
        val dueAtMillis = combineDateTimeMillis(state.selectedDateMillis, state.selectedHour, state.selectedMinute)
        return when {
            state.pets.isEmpty() -> {
                update { copy(validationError = "Add a pet before creating a reminder.") }
                null
            }
            petId == null -> {
                update { copy(validationError = "Choose a pet.") }
                null
            }
            state.title.trim().isBlank() -> {
                update { copy(validationError = "Reminder title is required.") }
                null
            }
            state.type !in ReminderTypes -> {
                update { copy(validationError = "Choose a reminder type.") }
                null
            }
            else -> ValidReminderForm(petId = petId, dueAtMillis = dueAtMillis)
        }
    }

    private fun update(block: AddReminderUiState.() -> AddReminderUiState) {
        _uiState.update(block)
    }
}

private data class ValidReminderForm(
    val petId: Long,
    val dueAtMillis: Long,
)

private fun dateOnlyMillis(millis: Long): Long =
    Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

private fun hourOfDay(millis: Long): Int =
    Calendar.getInstance().apply { timeInMillis = millis }.get(Calendar.HOUR_OF_DAY)

private fun minuteOfHour(millis: Long): Int =
    Calendar.getInstance().apply { timeInMillis = millis }.get(Calendar.MINUTE)

private fun combineDateTimeMillis(dateMillis: Long, hour: Int, minute: Int): Long =
    Calendar.getInstance().apply {
        timeInMillis = dateMillis
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
