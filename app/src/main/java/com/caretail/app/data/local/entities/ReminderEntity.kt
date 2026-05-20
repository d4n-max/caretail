package com.caretail.app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
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
        Index(value = ["dueAtMillis"]),
    ],
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val petId: Long,
    val title: String,
    val type: String,
    val notes: String? = null,
    val dueAtMillis: Long,
    val repeatType: String,
    val isCompleted: Boolean = false,
    val completedAtMillis: Long? = null,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
)
