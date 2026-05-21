package com.caretail.app.ui.model

import androidx.compose.ui.graphics.Color
import com.caretail.app.data.local.entities.HealthDiaryEntryEntity
import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.ui.theme.CareTailAccent
import com.caretail.app.ui.theme.CareTailBlue
import com.caretail.app.ui.theme.CareTailPrimaryDark
import com.caretail.app.ui.theme.CareTailWarning
import com.caretail.app.util.formatDiaryDate
import com.caretail.app.util.formatTime

data class HealthDiaryEntryUiModel(
    val id: Long,
    val petId: Long,
    val petName: String,
    val entryDateMillis: Long,
    val dateLabel: String,
    val timeLabel: String,
    val mood: String,
    val appetite: String,
    val energyLevel: String,
    val symptoms: String?,
    val notes: String?,
    val imageUri: String?,
    val moodColor: Color,
)

fun HealthDiaryEntryEntity.toUiModel(
    petName: String,
    nowMillis: Long = System.currentTimeMillis(),
): HealthDiaryEntryUiModel =
    HealthDiaryEntryUiModel(
        id = id,
        petId = petId,
        petName = petName,
        entryDateMillis = entryDateMillis,
        dateLabel = formatDiaryDate(entryDateMillis, nowMillis),
        timeLabel = formatTime(entryDateMillis),
        mood = mood,
        appetite = appetite,
        energyLevel = energyLevel,
        symptoms = symptoms,
        notes = notes,
        imageUri = imageUri,
        moodColor = when (mood) {
            "Great" -> CareTailPrimaryDark
            "Normal" -> CareTailBlue
            "Low" -> CareTailWarning
            else -> CareTailAccent
        },
    )

fun mapHealthDiaryEntryUiModels(
    entries: List<HealthDiaryEntryEntity>,
    pets: List<PetEntity>,
    nowMillis: Long = System.currentTimeMillis(),
): List<HealthDiaryEntryUiModel> {
    val petNames = pets.associate { it.id to it.name }
    return entries.map { entry ->
        entry.toUiModel(
            petName = petNames[entry.petId] ?: "Unknown pet",
            nowMillis = nowMillis,
        )
    }
}
