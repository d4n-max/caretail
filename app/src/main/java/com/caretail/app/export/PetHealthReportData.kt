package com.caretail.app.export

import com.caretail.app.data.local.entities.HealthDiaryEntryEntity
import com.caretail.app.data.local.entities.PetDocumentEntity
import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.data.local.entities.ReminderEntity

data class PetHealthReportData(
    val pet: PetEntity,
    val upcomingReminders: List<ReminderEntity>,
    val completedReminders: List<ReminderEntity>,
    val diaryEntries: List<HealthDiaryEntryEntity>,
    val documents: List<PetDocumentEntity>,
)
