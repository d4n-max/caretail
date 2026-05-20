package com.caretail.app.data.local.database

import com.caretail.app.data.local.entities.HealthDiaryEntryEntity
import com.caretail.app.data.local.entities.PetDocumentEntity
import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.data.local.entities.ReminderEntity

object SampleDataProvider {
    private const val LUNA_ID = 1L
    private const val MAX_ID = 2L

    fun pets(nowMillis: Long = System.currentTimeMillis()): List<PetEntity> = listOf(
        PetEntity(
            id = LUNA_ID,
            name = "Luna",
            species = "Cat",
            breed = "British Shorthair",
            gender = "Female",
            weightKg = 4.2,
            notes = "Indoor cat. Calm and food-motivated.",
            createdAtMillis = nowMillis,
            updatedAtMillis = nowMillis,
        ),
        PetEntity(
            id = MAX_ID,
            name = "Max",
            species = "Dog",
            breed = "Golden Retriever",
            gender = "Male",
            weightKg = 29.5,
            notes = "Enjoys evening walks and grooming sessions.",
            createdAtMillis = nowMillis,
            updatedAtMillis = nowMillis,
        ),
    )

    fun reminders(nowMillis: Long = System.currentTimeMillis()): List<ReminderEntity> = listOf(
        ReminderEntity(
            id = 1,
            petId = MAX_ID,
            title = "Heartworm Meds",
            type = "Medication",
            dueAtMillis = nowMillis,
            repeatType = "Monthly",
            createdAtMillis = nowMillis,
            updatedAtMillis = nowMillis,
        ),
        ReminderEntity(
            id = 2,
            petId = LUNA_ID,
            title = "Rabies Booster",
            type = "Vaccine",
            dueAtMillis = nowMillis,
            repeatType = "Yearly",
            createdAtMillis = nowMillis,
            updatedAtMillis = nowMillis,
        ),
        ReminderEntity(
            id = 3,
            petId = MAX_ID,
            title = "Grooming Session",
            type = "Grooming",
            dueAtMillis = nowMillis,
            repeatType = "None",
            createdAtMillis = nowMillis,
            updatedAtMillis = nowMillis,
        ),
    )

    fun healthDiaryEntries(nowMillis: Long = System.currentTimeMillis()): List<HealthDiaryEntryEntity> = listOf(
        HealthDiaryEntryEntity(
            id = 1,
            petId = LUNA_ID,
            entryDateMillis = nowMillis,
            mood = "Normal",
            appetite = "Normal",
            energyLevel = "Low",
            notes = "Ate breakfast and took a gentle stroll.",
            createdAtMillis = nowMillis,
            updatedAtMillis = nowMillis,
        ),
    )

    fun documents(nowMillis: Long = System.currentTimeMillis()): List<PetDocumentEntity> = listOf(
        PetDocumentEntity(
            id = 1,
            petId = LUNA_ID,
            title = "Vaccination_Record_2023.pdf",
            type = "Vaccine Record",
            createdAtMillis = nowMillis,
            updatedAtMillis = nowMillis,
        ),
    )
}
