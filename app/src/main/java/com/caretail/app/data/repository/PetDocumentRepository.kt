package com.caretail.app.data.repository

import com.caretail.app.data.local.dao.PetDocumentDao
import com.caretail.app.data.local.entities.PetDocumentEntity
import kotlinx.coroutines.flow.Flow

class PetDocumentRepository(
    private val petDocumentDao: PetDocumentDao,
) {
    fun observeDocumentsForPet(petId: Long): Flow<List<PetDocumentEntity>> =
        petDocumentDao.observeDocumentsForPet(petId)

    fun observeAllDocuments(): Flow<List<PetDocumentEntity>> = petDocumentDao.observeAllDocuments()

    suspend fun getDocumentById(id: Long): PetDocumentEntity? = petDocumentDao.getDocumentById(id)

    suspend fun addDocument(document: PetDocumentEntity): Long = petDocumentDao.insertDocument(document)

    suspend fun updateDocument(document: PetDocumentEntity) = petDocumentDao.updateDocument(document)

    suspend fun deleteDocument(document: PetDocumentEntity) = petDocumentDao.deleteDocument(document)
}
