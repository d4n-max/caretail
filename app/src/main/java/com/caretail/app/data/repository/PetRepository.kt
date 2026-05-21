package com.caretail.app.data.repository

import com.caretail.app.data.local.dao.PetDao
import com.caretail.app.data.local.entities.PetEntity
import kotlinx.coroutines.flow.Flow

class PetRepository(
    private val petDao: PetDao,
) {
    fun observeAllPets(): Flow<List<PetEntity>> = petDao.observeAllPets()

    fun observePetById(id: Long): Flow<PetEntity?> = petDao.observePetById(id)

    suspend fun getPetById(id: Long): PetEntity? = petDao.getPetById(id)

    suspend fun addPet(pet: PetEntity): Long = petDao.insertPet(pet)

    suspend fun updatePet(pet: PetEntity) = petDao.updatePet(pet)

    suspend fun deletePet(pet: PetEntity) = petDao.deletePet(pet)

    suspend fun deleteAllPets() = petDao.deleteAllPets()

    suspend fun getPetCount(): Int = petDao.getPetCount()
}
