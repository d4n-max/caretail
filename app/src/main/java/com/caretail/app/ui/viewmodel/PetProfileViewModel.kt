package com.caretail.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caretail.app.billing.PremiumManager
import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.data.repository.HealthDiaryRepository
import com.caretail.app.data.repository.PetDocumentRepository
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.data.repository.ReminderRepository
import com.caretail.app.export.PetHealthReportData
import com.caretail.app.export.PetHealthReportGenerator
import com.caretail.app.reminders.ReminderNotificationScheduler
import com.caretail.app.ui.model.HealthDiaryEntryUiModel
import com.caretail.app.ui.model.PetDocumentUiModel
import com.caretail.app.ui.model.ReminderUiModel
import com.caretail.app.ui.model.mapHealthDiaryEntryUiModels
import com.caretail.app.ui.model.mapPetDocumentUiModels
import com.caretail.app.ui.model.mapReminderUiModels
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PetProfileUiState(
    val pet: PetEntity? = null,
    val upcomingReminders: List<ReminderUiModel> = emptyList(),
    val recentDiaryEntries: List<HealthDiaryEntryUiModel> = emptyList(),
    val recentDocuments: List<PetDocumentUiModel> = emptyList(),
    val isPremium: Boolean = false,
    val isLoading: Boolean = true,
)

data class PetCareReportShareEvent(
    val petName: String,
    val reportText: String,
)

class PetProfileViewModel(
    private val petRepository: PetRepository,
    private val reminderRepository: ReminderRepository,
    private val healthDiaryRepository: HealthDiaryRepository,
    private val petDocumentRepository: PetDocumentRepository,
    private val reminderNotificationScheduler: ReminderNotificationScheduler,
    petId: Long,
) : ViewModel() {
    private val reportGenerator = PetHealthReportGenerator()
    private val _shareReportEvents = MutableSharedFlow<PetCareReportShareEvent>()
    val shareReportEvents: SharedFlow<PetCareReportShareEvent> = _shareReportEvents.asSharedFlow()
    private val _deletedEvents = MutableSharedFlow<Unit>()
    val deletedEvents: SharedFlow<Unit> = _deletedEvents.asSharedFlow()

    val uiState: StateFlow<PetProfileUiState> = combine(
        petRepository.observePetById(petId),
        reminderRepository.observeRemindersForPet(petId),
        healthDiaryRepository.observeEntriesForPet(petId),
        petDocumentRepository.observeDocumentsForPet(petId),
        PremiumManager.isPremium,
    ) { pet, reminders, diaryEntries, documents, isPremium ->
        val now = System.currentTimeMillis()
        val pets = pet?.let(::listOf) ?: emptyList()
        PetProfileUiState(
            pet = pet,
            upcomingReminders = mapReminderUiModels(reminders, pets, now)
                .filter { !it.isCompleted && !it.isOverdue }
                .sortedBy { it.dueAtMillis }
                .take(3),
            recentDiaryEntries = mapHealthDiaryEntryUiModels(diaryEntries.take(3), pets, now),
            recentDocuments = mapPetDocumentUiModels(documents.take(3), pets),
            isPremium = isPremium,
            isLoading = false,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PetProfileUiState(),
        )

    fun exportCareReport() {
        val state = uiState.value
        val pet = state.pet ?: return
        viewModelScope.launch {
            val reminders = reminderRepository.observeRemindersForPet(pet.id).first()
            val diaryEntries = healthDiaryRepository.observeEntriesForPet(pet.id).first()
            val documents = petDocumentRepository.observeDocumentsForPet(pet.id).first()
            val now = System.currentTimeMillis()
            val report = reportGenerator.generateTextReport(
                PetHealthReportData(
                    pet = pet,
                    upcomingReminders = reminders
                        .filter { !it.isCompleted && it.dueAtMillis >= now }
                        .sortedBy { it.dueAtMillis },
                    completedReminders = reminders
                        .filter { it.isCompleted }
                        .sortedByDescending { it.completedAtMillis ?: it.dueAtMillis },
                    diaryEntries = diaryEntries
                        .sortedByDescending { it.entryDateMillis }
                        .take(20),
                    documents = documents.sortedByDescending { it.createdAtMillis },
                ),
            )
            _shareReportEvents.emit(PetCareReportShareEvent(petName = pet.name, reportText = report))
        }
    }

    fun deletePetProfile() {
        val pet = uiState.value.pet ?: return
        viewModelScope.launch {
            reminderRepository.observeRemindersForPet(pet.id).first().forEach { reminder ->
                reminderNotificationScheduler.cancelReminder(reminder.id)
            }
            petRepository.deletePet(pet)
            _deletedEvents.emit(Unit)
        }
    }
}
