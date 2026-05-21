package com.caretail.app.data.repository

import com.caretail.app.data.local.dao.HealthDiaryDao
import com.caretail.app.data.local.entities.HealthDiaryEntryEntity
import kotlinx.coroutines.flow.Flow

class HealthDiaryRepository(
    private val healthDiaryDao: HealthDiaryDao,
) {
    fun observeAllEntries(): Flow<List<HealthDiaryEntryEntity>> = healthDiaryDao.observeAllEntries()

    fun observeEntriesForPet(petId: Long): Flow<List<HealthDiaryEntryEntity>> =
        healthDiaryDao.observeEntriesForPet(petId)

    fun observeRecentEntriesForPet(petId: Long, limit: Int): Flow<List<HealthDiaryEntryEntity>> =
        healthDiaryDao.observeRecentEntriesForPet(petId, limit)

    suspend fun getEntryById(id: Long): HealthDiaryEntryEntity? = healthDiaryDao.getEntryById(id)

    suspend fun getEntryCount(): Int = healthDiaryDao.getEntryCount()

    suspend fun addEntry(entry: HealthDiaryEntryEntity): Long = healthDiaryDao.insertEntry(entry)

    suspend fun updateEntry(entry: HealthDiaryEntryEntity) = healthDiaryDao.updateEntry(entry)

    suspend fun deleteEntry(entry: HealthDiaryEntryEntity) = healthDiaryDao.deleteEntry(entry)

    suspend fun deleteAllEntries() = healthDiaryDao.deleteAllEntries()
}
