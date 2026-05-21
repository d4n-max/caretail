package com.caretail.app.ui.screens.pets

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.MonitorWeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.caretail.app.billing.PremiumUpsellReason
import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.data.repository.HealthDiaryRepository
import com.caretail.app.data.repository.PetDocumentRepository
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.data.repository.ReminderRepository
import com.caretail.app.reminders.ReminderNotificationScheduler
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.CareTailBadge
import com.caretail.app.ui.components.CareTailScaffold
import com.caretail.app.ui.components.CareTailTopBar
import com.caretail.app.ui.components.PetAvatar
import com.caretail.app.ui.components.PrimaryCoralButton
import com.caretail.app.ui.components.SecondaryButton
import com.caretail.app.ui.components.SectionHeader
import com.caretail.app.ui.components.StatusPill
import com.caretail.app.ui.model.HealthDiaryEntryUiModel
import com.caretail.app.ui.model.PetDocumentUiModel
import com.caretail.app.ui.model.ReminderUiModel
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.theme.CareTailAccentSoft
import com.caretail.app.ui.theme.CareTailPrimary
import com.caretail.app.ui.theme.CareTailPrimaryDark
import com.caretail.app.ui.theme.CareTailTextPrimary
import com.caretail.app.ui.theme.CareTailTextSecondary
import com.caretail.app.ui.theme.CareTailWarmSurface
import com.caretail.app.ui.viewmodel.PetProfileViewModel
import com.caretail.app.ui.viewmodel.PetProfileViewModelFactory

@Composable
fun PetProfileScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    petRepository: PetRepository,
    reminderRepository: ReminderRepository,
    healthDiaryRepository: HealthDiaryRepository,
    petDocumentRepository: PetDocumentRepository,
    reminderNotificationScheduler: ReminderNotificationScheduler,
    petId: Long,
    onBack: () -> Unit,
    onAddReminder: (Long) -> Unit,
    onAddDiaryEntry: (Long) -> Unit,
    onAddDocument: (Long) -> Unit,
    onOpenPremium: (PremiumUpsellReason) -> Unit,
    onEditPet: (Long) -> Unit,
    onDeleted: () -> Unit,
) {
    val context = LocalContext.current
    val factory = remember(petRepository, reminderRepository, healthDiaryRepository, petDocumentRepository, reminderNotificationScheduler, petId) {
        PetProfileViewModelFactory(petRepository, reminderRepository, healthDiaryRepository, petDocumentRepository, reminderNotificationScheduler, petId)
    }
    val viewModel: PetProfileViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val pet = uiState.pet
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.shareReportEvents.collect { event ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "CareTail report for ${event.petName}")
                putExtra(Intent.EXTRA_TEXT, event.reportText)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share care report"))
        }
    }
    LaunchedEffect(viewModel) {
        viewModel.deletedEvents.collect { onDeleted() }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete pet profile?") },
            text = { Text("This will remove this pet and its reminders, health notes, and document records from this device.") },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deletePetProfile()
                }) { Text("Delete", color = com.caretail.app.ui.theme.CareTailAccent) }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            },
        )
    }

    CareTailScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        selectedBottomRoute = CareTailRoute.Pets.route,
        topBar = {
            CareTailTopBar(
                title = pet?.let { "${it.name}'s Profile" } ?: "Pet Profile",
                showBack = true,
                showMenu = true,
                onBack = onBack,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            when {
                uiState.isLoading -> Text("Loading pet...", style = MaterialTheme.typography.bodyLarge)
                pet == null -> MissingPetCard(onBack = onBack)
                else -> PetProfileContent(
                    pet = pet,
                    reminders = uiState.upcomingReminders,
                    diaryEntries = uiState.recentDiaryEntries,
                    documents = uiState.recentDocuments,
                    isPremium = uiState.isPremium,
                    onAddReminder = { onAddReminder(pet.id) },
                    onAddDiaryEntry = { onAddDiaryEntry(pet.id) },
                    onAddDocument = { onAddDocument(pet.id) },
                    onEditReminder = { reminderId -> onNavigate(CareTailRoute.EditReminder.createRoute(reminderId)) },
                    onEditDiaryEntry = { entryId -> onNavigate(CareTailRoute.EditDiaryEntry.createRoute(entryId)) },
                    onEditDocument = { documentId -> onNavigate(CareTailRoute.EditDocument.createRoute(documentId)) },
                    onEditPet = { onEditPet(pet.id) },
                    onDeletePet = { showDeleteDialog = true },
                    onExportReport = {
                        if (uiState.isPremium) {
                            viewModel.exportCareReport()
                        } else {
                            onOpenPremium(PremiumUpsellReason.ExportLocked)
                        }
                    },
                )
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PetProfileContent(
    pet: PetEntity,
    reminders: List<ReminderUiModel>,
    diaryEntries: List<HealthDiaryEntryUiModel>,
    documents: List<PetDocumentUiModel>,
    isPremium: Boolean,
    onAddReminder: () -> Unit,
    onAddDiaryEntry: () -> Unit,
    onAddDocument: () -> Unit,
    onEditReminder: (Long) -> Unit,
    onEditDiaryEntry: (Long) -> Unit,
    onEditDocument: (Long) -> Unit,
    onEditPet: () -> Unit,
    onDeletePet: () -> Unit,
    onExportReport: () -> Unit,
) {
    CareTailCard(modifier = Modifier.fillMaxWidth(), backgroundColor = CareTailWarmSurface) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PetAvatar(name = pet.name, size = 108.dp, backgroundColor = CareTailPrimary.copy(alpha = 0.18f))
            Spacer(Modifier.height(16.dp))
            Text(pet.name, style = MaterialTheme.typography.headlineMedium, color = CareTailTextPrimary)
            Text(
                listOfNotNull(pet.species, pet.breed).joinToString(" - "),
                style = MaterialTheme.typography.bodyLarge,
                color = CareTailTextSecondary,
            )
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatusPill(pet.species, backgroundColor = CareTailPrimary.copy(alpha = 0.18f), contentColor = CareTailPrimaryDark)
                pet.gender?.let { gender ->
                    StatusPill(gender, backgroundColor = CareTailAccentSoft, contentColor = CareTailTextPrimary)
                }
                pet.weightKg?.let { weight ->
                    StatusPill("${weight} kg", backgroundColor = CareTailAccentSoft, contentColor = CareTailTextPrimary, icon = Icons.Rounded.MonitorWeight)
                }
            }
            pet.notes?.let { notes ->
                Spacer(Modifier.height(14.dp))
                Text(notes, style = MaterialTheme.typography.bodyMedium, color = CareTailTextSecondary)
            }
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PrimaryCoralButton(text = "Edit Profile", modifier = Modifier.weight(1f), onClick = onEditPet)
                ExportReportButton(
                    showPremiumBadge = !isPremium,
                    modifier = Modifier.weight(1f),
                    onClick = onExportReport,
                )
            }
            Spacer(Modifier.height(10.dp))
            SecondaryButton(text = "Delete Pet", modifier = Modifier.fillMaxWidth(), onClick = onDeletePet)
        }
    }
    Spacer(Modifier.height(22.dp))
    UpcomingRemindersSection(reminders = reminders, onAddReminder = onAddReminder, onEditReminder = onEditReminder)
    Spacer(Modifier.height(12.dp))
    RecentDiarySection(entries = diaryEntries, onAddDiaryEntry = onAddDiaryEntry, onEditDiaryEntry = onEditDiaryEntry)
    Spacer(Modifier.height(12.dp))
    DocumentsSection(documents = documents, onAddDocument = onAddDocument, onEditDocument = onEditDocument)
}

@Composable
private fun ExportReportButton(
    showPremiumBadge: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(modifier = modifier) {
        SecondaryButton(
            text = "Export Report",
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick,
        )
        if (showPremiumBadge) {
            CareTailBadge(
                text = "Premium",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(y = (-12).dp)
                    .padding(end = 8.dp),
            )
        }
    }
}

@Composable
private fun UpcomingRemindersSection(
    reminders: List<ReminderUiModel>,
    onAddReminder: () -> Unit,
    onEditReminder: (Long) -> Unit,
) {
    SectionHeader("Upcoming Reminders", icon = Icons.Rounded.Event, actionText = "Add", onAction = onAddReminder)
    Spacer(Modifier.height(12.dp))
    if (reminders.isEmpty()) {
        CareTailCard {
            Text(
                "Reminders will appear here.",
                style = MaterialTheme.typography.bodyLarge,
                color = CareTailTextSecondary,
                fontWeight = FontWeight.Normal,
            )
        }
    } else {
        reminders.forEach { reminder ->
            CareTailCard(modifier = Modifier.clickable { onEditReminder(reminder.id) }) {
                Text(reminder.title, style = MaterialTheme.typography.titleMedium, color = CareTailTextPrimary)
                Text(
                    "${reminder.dueDateLabel} at ${reminder.dueTimeLabel}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CareTailTextSecondary,
                )
                Spacer(Modifier.height(10.dp))
                StatusPill(reminder.type)
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun DocumentsSection(
    documents: List<PetDocumentUiModel>,
    onAddDocument: () -> Unit,
    onEditDocument: (Long) -> Unit,
) {
    SectionHeader("Documents & Records", icon = Icons.Rounded.Description, actionText = "Add document", onAction = onAddDocument)
    Spacer(Modifier.height(12.dp))
    if (documents.isEmpty()) {
        CareTailCard {
            Text("Documents will appear here.", style = MaterialTheme.typography.bodyLarge, color = CareTailTextSecondary, fontWeight = FontWeight.Normal)
        }
    } else {
        documents.forEach { document ->
            CareTailCard(modifier = Modifier.clickable { onEditDocument(document.id) }) {
                Text(document.title, style = MaterialTheme.typography.titleMedium, color = CareTailTextPrimary)
                Text(document.createdDateLabel, style = MaterialTheme.typography.bodyMedium, color = CareTailTextSecondary)
                Spacer(Modifier.height(10.dp))
                StatusPill(document.type)
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun RecentDiarySection(
    entries: List<HealthDiaryEntryUiModel>,
    onAddDiaryEntry: () -> Unit,
    onEditDiaryEntry: (Long) -> Unit,
) {
    SectionHeader("Recent Health Diary", icon = Icons.Rounded.Favorite, actionText = "Log health", onAction = onAddDiaryEntry)
    Spacer(Modifier.height(12.dp))
    if (entries.isEmpty()) {
        CareTailCard {
            Text(
                "Health notes will appear here.",
                style = MaterialTheme.typography.bodyLarge,
                color = CareTailTextSecondary,
                fontWeight = FontWeight.Normal,
            )
        }
    } else {
        entries.forEach { entry ->
            CareTailCard(modifier = Modifier.clickable { onEditDiaryEntry(entry.id) }) {
                Text("${entry.dateLabel} at ${entry.timeLabel}", style = MaterialTheme.typography.labelLarge, color = CareTailTextSecondary)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusPill(entry.mood, contentColor = entry.moodColor)
                    StatusPill(entry.appetite)
                    StatusPill(entry.energyLevel)
                }
                entry.notes?.let { notes ->
                    Spacer(Modifier.height(8.dp))
                    Text(notes, style = MaterialTheme.typography.bodyMedium, color = CareTailTextPrimary, maxLines = 2)
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun MissingPetCard(onBack: () -> Unit) {
    CareTailCard(modifier = Modifier.fillMaxWidth(), backgroundColor = CareTailWarmSurface) {
        Text("Pet not found", style = MaterialTheme.typography.titleLarge, color = CareTailTextPrimary)
        Spacer(Modifier.height(8.dp))
        Text("This pet profile is no longer available.", style = MaterialTheme.typography.bodyMedium, color = CareTailTextSecondary)
        Spacer(Modifier.height(16.dp))
        PrimaryCoralButton(text = "Back to Pets", onClick = onBack)
    }
}

@Composable
private fun EmptyProfileSection(
    title: String,
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    SectionHeader(title, icon = icon)
    Spacer(Modifier.height(12.dp))
    CareTailCard {
        Text(message, style = MaterialTheme.typography.bodyLarge, color = CareTailTextSecondary, fontWeight = FontWeight.Normal)
    }
}
