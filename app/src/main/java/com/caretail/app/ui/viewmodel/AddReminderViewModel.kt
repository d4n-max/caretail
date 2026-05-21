package com.caretail.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.data.local.entities.ReminderEntity
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.data.repository.ReminderRepository
import com.caretail.app.util.defaultReminderDueAtMillis
import com.caretail.app.util.formatInputDate
import com.caretail.app.util.formatInputTime
import com.caretail.app.util.parseDateTimeMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val ReminderTypes = listOf("Vaccine", "Medication", "Vet Visit", "Grooming", "Food", "Other")
val RepeatTypes = listOf("None", "Daily", "Weekly", "Monthly", "Yearly")

data class AddReminderUiState(
    val pets: List<PetEntity> = emptyList(),
    val selectedPetId: Long? = null,
    val title: String = "",
    val type: String = ReminderTypes.first(),
    val date: String = formatInputDate(defaultReminderDueAtMillis()),
    val time: String = formatInputTime(defaultReminderDueAtMillis()),
    val repeatType: String = RepeatTypes.first(),
    val notes: String = "",
    val isLoading: Boolean = true,
    val success: Boolean = false,
    val validationError: String? = null,
    val generalError: String? = null,
)

class AddReminderViewModel(
    private val petRepository: PetRepository,
    private val reminderRepository: ReminderRepository,
    private val preselectedPetId: Long? = null,
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
    }

    fun onPetSelected(petId: Long) = update { copy(selectedPetId = petId, validationError = null, generalError = null) }

    fun onTitleChanged(value: String) = update { copy(title = value, validationError = null, generalError = null) }

    fun onTypeSelected(value: String) = update { copy(type = value, validationError = null, generalError = null) }

    fun onDateChanged(value: String) = update { copy(date = value, validationError = null, generalError = null) }

    fun onTimeChanged(value: String) = update { copy(time = value, validationError = null, generalError = null) }

    fun onRepeatTypeSelected(value: String) = update { copy(repeatType = value, validationError = null, generalError = null) }

    fun onNotesChanged(value: String) = update { copy(notes = value) }

    fun saveReminder() {
        val state = uiState.value
        val petId = state.selectedPetId
        val dueAtMillis = parseDateTimeMillis(state.date, state.time)
        when {
            state.pets.isEmpty() -> {
                update { copy(validationError = "Add a pet before creating a reminder.") }
                return
            }
            petId == null -> {
                update { copy(validationError = "Choose a pet.") }
                return
            }
            state.title.trim().isBlank() -> {
                update { copy(validationError = "Reminder title is required.") }
                return
            }
            state.type !in ReminderTypes -> {
                update { copy(validationError = "Choose a reminder type.") }
                return
            }
            dueAtMillis == null -> {
                update { copy(validationError = "Use date as YYYY-MM-DD and time as HH:mm.") }
                return
            }
        }

        viewModelScope.launch {
            update { copy(isLoading = true, validationError = null, generalError = null) }
            try {
                val now = System.currentTimeMillis()
                reminderRepository.addReminder(
                    ReminderEntity(
                        petId = petId,
                        title = state.title.trim(),
                        type = state.type,
                        notes = state.notes.trim().ifBlank { null },
                        dueAtMillis = dueAtMillis,
                        repeatType = state.repeatType,
                        isCompleted = false,
                        completedAtMillis = null,
                        createdAtMillis = now,
                        updatedAtMillis = now,
                    ),
                )
                update { copy(isLoading = false, success = true) }
            } catch (error: Exception) {
                update { copy(isLoading = false, generalError = error.message ?: "Unable to save reminder.") }
            }
        }
    }

    private fun update(block: AddReminderUiState.() -> AddReminderUiState) {
        _uiState.update(block)
    }
}
