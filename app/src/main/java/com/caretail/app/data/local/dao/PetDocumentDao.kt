package com.caretail.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.caretail.app.data.local.entities.PetDocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDocumentDao {
    @Query("SELECT * FROM pet_documents WHERE petId = :petId ORDER BY createdAtMillis DESC")
    fun observeDocumentsForPet(petId: Long): Flow<List<PetDocumentEntity>>

    @Query("SELECT * FROM pet_documents ORDER BY createdAtMillis DESC")
    fun observeAllDocuments(): Flow<List<PetDocumentEntity>>

    @Query("SELECT * FROM pet_documents WHERE petId = :petId ORDER BY createdAtMillis DESC LIMIT :limit")
    fun observeRecentDocumentsForPet(petId: Long, limit: Int): Flow<List<PetDocumentEntity>>

    @Query("SELECT * FROM pet_documents WHERE id = :id LIMIT 1")
    suspend fun getDocumentById(id: Long): PetDocumentEntity?

    @Query("SELECT COUNT(*) FROM pet_documents")
    suspend fun getDocumentCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: PetDocumentEntity): Long

    @Update
    suspend fun updateDocument(document: PetDocumentEntity)

    @Delete
    suspend fun deleteDocument(document: PetDocumentEntity)
}
