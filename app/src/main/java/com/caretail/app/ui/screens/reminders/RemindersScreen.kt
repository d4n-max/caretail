package com.caretail.app.ui.screens.reminders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Vaccines
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.data.repository.ReminderRepository
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.CareTailScaffold
import com.caretail.app.ui.components.CareTailTopBar
import com.caretail.app.ui.components.CoralFab
import com.caretail.app.ui.components.InfoRow
import com.caretail.app.ui.components.PrimaryCoralButton
import com.caretail.app.ui.components.SectionHeader
import com.caretail.app.ui.components.StatusPill
import com.caretail.app.ui.model.ReminderUiModel
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.theme.CareTailAccent
import com.caretail.app.ui.theme.CareTailAccentSoft
import com.caretail.app.ui.theme.CareTailPrimary
import com.caretail.app.ui.theme.CareTailPrimaryDark
import com.caretail.app.ui.theme.CareTailTextPrimary
import com.caretail.app.ui.theme.CareTailTextSecondary
import com.caretail.app.ui.theme.CareTailWarmSurface
import com.caretail.app.ui.viewmodel.RemindersViewModel
import com.caretail.app.ui.viewmodel.RemindersViewModelFactory

@Composable
fun RemindersScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    reminderRepository: ReminderRepository,
    petRepository: PetRepository,
    onAddReminder: () -> Unit,
) {
    val factory = remember(reminderRepository, petRepository) {
        RemindersViewModelFactory(reminderRepository, petRepository)
    }
    val viewModel: RemindersViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    CareTailScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        selectedBottomRoute = CareTailRoute.Reminders.route,
        topBar = { CareTailTopBar(title = "Reminders") },
        floatingActionButton = { CoralFab(onClick = onAddReminder) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            if (!uiState.isLoading && !uiState.hasAnyReminder) {
                EmptyRemindersState(onAddReminder = onAddReminder)
            } else {
                ReminderSection(
                    title = "Overdue",
                    reminders = uiState.overdue,
                    emptyText = null,
                    onToggle = { viewModel.markCompleted(it.id) },
                    onDelete = viewModel::deleteReminder,
                )
                ReminderSection(
                    title = "Today",
                    reminders = uiState.today,
                    emptyText = null,
                    onToggle = { viewModel.markCompleted(it.id) },
                    onDelete = viewModel::deleteReminder,
                )
                ReminderSection(
                    title = "Upcoming",
                    reminders = uiState.upcoming,
                    emptyText = null,
                    onToggle = { viewModel.markCompleted(it.id) },
                    onDelete = viewModel::deleteReminder,
                )
                ReminderSection(
                    title = "Completed",
                    reminders = uiState.completed,
                    emptyText = null,
                    onToggle = { viewModel.markIncomplete(it.id) },
                    onDelete = viewModel::deleteReminder,
                )
            }
            Spacer(Modifier.height(90.dp))
        }
    }
}

@Composable
private fun EmptyRemindersState(onAddReminder: () -> Unit) {
    CareTailCard(backgroundColor = CareTailWarmSurface) {
        Text("No care reminders yet", style = MaterialTheme.typography.titleLarge, color = CareTailTextPrimary)
        Spacer(Modifier.height(8.dp))
        Text(
            "Add vaccines, medication, grooming, food, or vet visit reminders.",
            style = MaterialTheme.typography.bodyLarge,
            color = CareTailTextSecondary,
        )
        Spacer(Modifier.height(16.dp))
        PrimaryCoralButton(text = "Add Reminder", onClick = onAddReminder)
    }
}

@Composable
private fun ReminderSection(
    title: String,
    reminders: List<ReminderUiModel>,
    emptyText: String?,
    onToggle: (ReminderUiModel) -> Unit,
    onDelete: (ReminderUiModel) -> Unit,
) {
    if (reminders.isEmpty() && emptyText == null) return
    SectionHeader(title)
    Spacer(Modifier.height(12.dp))
    if (reminders.isEmpty()) {
        CareTailCard {
            Text(emptyText.orEmpty(), style = MaterialTheme.typography.bodyLarge, color = CareTailTextSecondary)
        }
    } else {
        reminders.forEach { reminder ->
            ReminderCard(
                reminder = reminder,
                icon = reminderIcon(reminder.type),
                onToggle = { onToggle(reminder) },
                onDelete = { onDelete(reminder) },
            )
            Spacer(Modifier.height(10.dp))
        }
    }
    Spacer(Modifier.height(14.dp))
}

@Composable
private fun ReminderCard(
    reminder: ReminderUiModel,
    icon: ImageVector,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
) {
    CareTailCard(modifier = Modifier.fillMaxWidth()) {
        InfoRow(
            title = reminder.title,
            subtitle = "${reminder.petName} - ${reminder.dueDateLabel} at ${reminder.dueTimeLabel}",
            icon = icon,
            trailing = {
                Row {
                    IconButton(onClick = onToggle) {
                        Icon(
                            imageVector = if (reminder.isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                            contentDescription = if (reminder.isCompleted) "Mark incomplete" else "Mark complete",
                            tint = if (reminder.isCompleted) CareTailPrimaryDark else CareTailAccent,
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = CareTailTextSecondary)
                    }
                }
            },
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusPill(
                text = if (reminder.isCompleted) "Done" else reminder.type,
                backgroundColor = if (reminder.isCompleted) CareTailPrimary.copy(alpha = 0.16f) else CareTailAccentSoft,
                contentColor = if (reminder.isCompleted) CareTailPrimaryDark else CareTailAccent,
            )
            if (reminder.repeatType != "None") {
                StatusPill(text = reminder.repeatType)
            }
        }
        reminder.notes?.let { notes ->
            Spacer(Modifier.height(10.dp))
            Text(
                notes,
                style = MaterialTheme.typography.bodyMedium,
                color = CareTailTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun reminderIcon(type: String): ImageVector = when (type) {
    "Vaccine" -> Icons.Rounded.Vaccines
    "Medication" -> Icons.Rounded.Medication
    "Vet Visit" -> Icons.Rounded.Event
    "Food" -> Icons.Rounded.Restaurant
    else -> Icons.Rounded.Pets
}
