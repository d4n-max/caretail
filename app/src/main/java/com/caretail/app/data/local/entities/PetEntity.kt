package com.caretail.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class PetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val species: String,
    val breed: String? = null,
    val gender: String? = null,
    val birthDateMillis: Long? = null,
    val weightKg: Double? = null,
    val photoUri: String? = null,
    val notes: String? = null,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
)
