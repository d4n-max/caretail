package com.caretail.app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pet_documents",
    foreignKeys = [
        ForeignKey(
            entity = PetEntity::class,
            parentColumns = ["id"],
            childColumns = ["petId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["petId"]),
    ],
)
data class PetDocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val petId: Long,
    val title: String,
    val type: String,
    val fileUri: String? = null,
    val notes: String? = null,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
)
