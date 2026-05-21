package com.caretail.app.export

import com.caretail.app.data.local.entities.HealthDiaryEntryEntity
import com.caretail.app.data.local.entities.PetDocumentEntity
import com.caretail.app.data.local.entities.ReminderEntity
import com.caretail.app.util.formatDate
import com.caretail.app.util.formatTime

class PetHealthReportGenerator {
    fun generateTextReport(data: PetHealthReportData): String = buildString {
        appendLine("CareTail Pet Care Report")
        appendLine("Generated: ${formatDate(System.currentTimeMillis())} ${formatTime(System.currentTimeMillis())}")
        appendLine()
        appendLine("Pet Profile")
        appendLine("- Name: ${data.pet.name}")
        appendLine("- Species: ${data.pet.species}")
        appendLine("- Breed: ${data.pet.breed.orEmptyText()}")
        appendLine("- Gender: ${data.pet.gender.orEmptyText()}")
        appendLine("- Weight: ${data.pet.weightKg?.let { "$it kg" } ?: "Not recorded"}")
        appendLine("- Notes: ${data.pet.notes.orEmptyText()}")
        appendLine()
        appendReminderSection("Upcoming Reminders", data.upcomingReminders, emptyText = "No upcoming reminders.")
        appendLine()
        appendReminderSection("Completed Reminders", data.completedReminders, emptyText = "No completed reminders.")
        appendLine()
        appendDiarySection(data.diaryEntries)
        appendLine()
        appendDocumentSection(data.documents)
        appendLine()
        appendLine("This report is for personal pet care organization only and does not replace veterinary advice.")
    }

    private fun StringBuilder.appendReminderSection(
        title: String,
        reminders: List<ReminderEntity>,
        emptyText: String,
    ) {
        appendLine(title)
        if (reminders.isEmpty()) {
            appendLine("- $emptyText")
            return
        }
        reminders.forEach { reminder ->
            appendLine("- ${formatDate(reminder.dueAtMillis)} ${formatTime(reminder.dueAtMillis)} - ${reminder.title} - ${reminder.type}")
        }
    }

    private fun StringBuilder.appendDiarySection(entries: List<HealthDiaryEntryEntity>) {
        appendLine("Health Diary")
        if (entries.isEmpty()) {
            appendLine("- No health notes yet.")
            return
        }
        entries.forEach { entry ->
            appendLine("- ${formatDate(entry.entryDateMillis)} ${formatTime(entry.entryDateMillis)}")
            appendLine("  Mood: ${entry.mood}")
            appendLine("  Appetite: ${entry.appetite}")
            appendLine("  Energy: ${entry.energyLevel}")
            appendLine("  Symptoms: ${entry.symptoms.orEmptyText()}")
            appendLine("  Notes: ${entry.notes.orEmptyText()}")
        }
    }

    private fun StringBuilder.appendDocumentSection(documents: List<PetDocumentEntity>) {
        appendLine("Documents & Records")
        if (documents.isEmpty()) {
            appendLine("- No documents yet.")
            return
        }
        documents.forEach { document ->
            appendLine("- ${document.title} - ${document.type}")
            document.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                appendLine("  Notes: $notes")
            }
        }
    }

    private fun String?.orEmptyText(): String = this?.takeIf { it.isNotBlank() } ?: "Not recorded"
}
