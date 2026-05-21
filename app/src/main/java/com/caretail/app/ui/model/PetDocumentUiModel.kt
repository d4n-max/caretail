package com.caretail.app.ui.model

import com.caretail.app.data.local.entities.PetDocumentEntity
import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.util.formatDocumentDate

data class PetDocumentUiModel(
    val id: Long,
    val petId: Long,
    val petName: String,
    val title: String,
    val type: String,
    val fileUri: String?,
    val notes: String?,
    val createdAtMillis: Long,
    val createdDateLabel: String,
)

fun PetDocumentEntity.toUiModel(petName: String): PetDocumentUiModel =
    PetDocumentUiModel(
        id = id,
        petId = petId,
        petName = petName,
        title = title,
        type = type,
        fileUri = fileUri,
        notes = notes,
        createdAtMillis = createdAtMillis,
        createdDateLabel = formatDocumentDate(createdAtMillis),
    )

fun mapPetDocumentUiModels(
    documents: List<PetDocumentEntity>,
    pets: List<PetEntity>,
): List<PetDocumentUiModel> {
    val petNames = pets.associate { it.id to it.name }
    return documents.map { document ->
        document.toUiModel(petName = petNames[document.petId] ?: "Unknown pet")
    }
}
