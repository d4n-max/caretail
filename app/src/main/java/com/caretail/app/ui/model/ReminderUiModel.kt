package com.caretail.app.ui.model

import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.data.local.entities.ReminderEntity
import com.caretail.app.util.formatDate
import com.caretail.app.util.formatTime
import com.caretail.app.util.isOverdue

data class ReminderUiModel(
    val id: Long,
    val petId: Long,
    val petName: String,
    val title: String,
    val type: String,
    val notes: String?,
    val dueAtMillis: Long,
    val dueDateLabel: String,
    val dueTimeLabel: String,
    val repeatType: String,
    val isCompleted: Boolean,
    val isOverdue: Boolean,
)

fun ReminderEntity.toUiModel(petName: String, nowMillis: Long = System.currentTimeMillis()): ReminderUiModel =
    ReminderUiModel(
        id = id,
        petId = petId,
        petName = petName,
        title = title,
        type = type,
        notes = notes,
        dueAtMillis = dueAtMillis,
        dueDateLabel = formatDate(dueAtMillis),
        dueTimeLabel = formatTime(dueAtMillis),
        repeatType = repeatType,
        isCompleted = isCompleted,
        isOverdue = isOverdue(dueAtMillis, isCompleted, nowMillis),
    )

fun mapReminderUiModels(
    reminders: List<ReminderEntity>,
    pets: List<PetEntity>,
    nowMillis: Long = System.currentTimeMillis(),
): List<ReminderUiModel> {
    val petNames = pets.associate { it.id to it.name }
    return reminders.map { reminder ->
        reminder.toUiModel(
            petName = petNames[reminder.petId] ?: "Unknown pet",
            nowMillis = nowMillis,
        )
    }
}
