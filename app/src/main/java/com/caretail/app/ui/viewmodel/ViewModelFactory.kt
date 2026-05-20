package com.caretail.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.caretail.app.data.repository.PetRepository

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
    private val petId: Long,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PetProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PetProfileViewModel(petRepository, petId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class HomeViewModelFactory(
    private val petRepository: PetRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(petRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
