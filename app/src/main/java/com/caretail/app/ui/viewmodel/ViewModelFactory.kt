package com.caretail.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.data.repository.ReminderRepository
import com.caretail.app.reminders.ReminderNotificationScheduler

class AddPetViewModelFactory(
    private val petRepository: PetRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddPetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddPetViewModel(petRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class PetsViewModelFactory(
    private val petRepository: PetRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PetsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PetsViewModel(petRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class PetProfileViewModelFactory(
    private val petRepository: PetRepository,
    private val reminderRepository: ReminderRepository,
    private val petId: Long,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PetProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PetProfileViewModel(petRepository, reminderRepository, petId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class HomeViewModelFactory(
    private val petRepository: PetRepository,
    private val reminderRepository: ReminderRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(petRepository, reminderRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class AddReminderViewModelFactory(
    private val petRepository: PetRepository,
    private val reminderRepository: ReminderRepository,
    private val reminderNotificationScheduler: ReminderNotificationScheduler,
    private val preselectedPetId: Long? = null,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddReminderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddReminderViewModel(petRepository, reminderRepository, reminderNotificationScheduler, preselectedPetId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class RemindersViewModelFactory(
    private val reminderRepository: ReminderRepository,
    private val reminderNotificationScheduler: ReminderNotificationScheduler,
    private val petRepository: PetRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RemindersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RemindersViewModel(reminderRepository, reminderNotificationScheduler, petRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
