package com.caretail.app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "health_diary_entries",
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
        Index(value = ["entryDateMillis"]),
    ],
)
data class HealthDiaryEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val petId: Long,
    val entryDateMillis: Long,
    val mood: String,
    val appetite: String,
    val energyLevel: String,
    val symptoms: String? = null,
    val notes: String? = null,
    val imageUri: String? = null,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
)
