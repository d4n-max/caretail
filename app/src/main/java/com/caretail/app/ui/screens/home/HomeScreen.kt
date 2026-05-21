package com.caretail.app.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.caretail.app.data.local.entities.PetEntity
import com.caretail.app.data.repository.HealthDiaryRepository
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.data.repository.ReminderRepository
import com.caretail.app.ui.components.AddPetIcon
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.CareTailScaffold
import com.caretail.app.ui.components.CareTailTopBar
import com.caretail.app.ui.components.DocumentIcon
import com.caretail.app.ui.components.HealthIcon
import com.caretail.app.ui.components.InfoRow
import com.caretail.app.ui.components.PetAvatar
import com.caretail.app.ui.components.PrimaryCoralButton
import com.caretail.app.ui.components.QuickActionCard
import com.caretail.app.ui.components.ReminderIcon
import com.caretail.app.ui.components.SectionHeader
import com.caretail.app.ui.components.StatusPill
import com.caretail.app.ui.model.HealthDiaryEntryUiModel
import com.caretail.app.ui.model.ReminderUiModel
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.theme.CareTailAccent
import com.caretail.app.ui.theme.CareTailAccentSoft
import com.caretail.app.ui.theme.CareTailCard as CareTailCardColor
import com.caretail.app.ui.theme.CareTailPrimary
import com.caretail.app.ui.theme.CareTailPrimaryDark
import com.caretail.app.ui.theme.CareTailTextPrimary
import com.caretail.app.ui.theme.CareTailTextSecondary
import com.caretail.app.ui.theme.CareTailWarmSurface
import com.caretail.app.ui.viewmodel.HomeViewModel
import com.caretail.app.ui.viewmodel.HomeViewModelFactory

@Composable
fun HomeScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    petRepository: PetRepository,
    reminderRepository: ReminderRepository,
    healthDiaryRepository: HealthDiaryRepository,
    onOpenPremium: () -> Unit,
    onAddPet: () -> Unit,
    onOpenPetProfile: (Long) -> Unit,
    onAddReminder: () -> Unit,
    onAddDiaryEntry: () -> Unit,
    onAddDocument: () -> Unit,
) {
    val factory = remember(petRepository, reminderRepository, healthDiaryRepository) {
        HomeViewModelFactory(petRepository, reminderRepository, healthDiaryRepository)
    }
    val viewModel: HomeViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    CareTailScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        selectedBottomRoute = CareTailRoute.Home.route,
        topBar = {
            CareTailTopBar(
                title = "CareTail",
                showAvatar = true,
                showSettings = true,
                onSettings = { onNavigate(CareTailRoute.Settings.route) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
        ) {
            Text("Welcome back", style = MaterialTheme.typography.headlineMedium, color = CareTailTextPrimary)
            Text(
                "Here is the latest on your furry friends.",
                style = MaterialTheme.typography.bodyLarge,
                color = CareTailTextSecondary,
            )
            Spacer(Modifier.height(22.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                if (uiState.pets.isEmpty()) {
                    AddFirstPetCard(onClick = onAddPet)
                } else {
                    uiState.pets.forEachIndexed { index, pet ->
                        PetSummaryCard(
                            pet = pet,
                            avatarColor = if (index % 2 == 0) CareTailPrimary.copy(alpha = 0.22f) else CareTailWarmSurface,
                            onClick = { onOpenPetProfile(pet.id) },
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            SectionHeader("Quick Actions")
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionCard(
                    "Add Pet",
                    AddPetIcon,
                    Modifier.weight(1f),
                    onClick = {
                        if (uiState.canAddPet) {
                            onAddPet()
                        } else {
                            onOpenPremium()
                        }
                    },
                )
                QuickActionCard("Reminder", ReminderIcon, Modifier.weight(1f), onClick = onAddReminder)
                QuickActionCard("Log Health", HealthIcon, Modifier.weight(1f), onClick = onAddDiaryEntry)
                QuickActionCard("Document", DocumentIcon, Modifier.weight(1f), onClick = onAddDocument)
            }
            Spacer(Modifier.height(24.dp))
            SectionHeader("Today's Care", icon = Icons.Rounded.Event)
            Spacer(Modifier.height(12.dp))
            if (uiState.todayReminders.isEmpty()) {
                EmptySmallCard("No care due today")
            } else {
                uiState.todayReminders.forEach { reminder ->
                    CareTaskCard(reminder)
                    Spacer(Modifier.height(10.dp))
                }
            }
            Spacer(Modifier.height(24.dp))
            SectionHeader("Upcoming")
            Spacer(Modifier.height(12.dp))
            if (uiState.upcomingReminders.isEmpty()) {
                EmptySmallCard("No upcoming reminders")
            } else {
                uiState.upcomingReminders.forEach { reminder ->
                    UpcomingCard(reminder)
                    Spacer(Modifier.height(10.dp))
                }
            }
            Spacer(Modifier.height(18.dp))
            SectionHeader("Latest Health Note")
            Spacer(Modifier.height(12.dp))
            uiState.latestDiaryEntry?.let { entry ->
                LatestDiaryCard(entry = entry)
            } ?: EmptySmallCard("Log your first health note")
            Spacer(Modifier.height(18.dp))
            CareTailCard(backgroundColor = CareTailWarmSurface) {
                Text("CareTail Premium", style = MaterialTheme.typography.titleLarge, color = CareTailPrimaryDark)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Unlimited pets, advanced reminders, and exportable reports when you are ready.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CareTailTextSecondary,
                )
                Spacer(Modifier.height(14.dp))
                PrimaryCoralButton(text = "View Premium", onClick = onOpenPremium)
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PetSummaryCard(
    pet: PetEntity,
    avatarColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    CareTailCard(modifier = Modifier.width(248.dp).clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            PetAvatar(name = pet.name, size = 62.dp, backgroundColor = avatarColor)
            Column {
                Text(pet.name, style = MaterialTheme.typography.titleLarge, color = CareTailTextPrimary)
                Text(petSubtitle(pet), style = MaterialTheme.typography.bodyMedium, color = CareTailTextSecondary)
            }
        }
        Spacer(Modifier.height(14.dp))
        StatusPill(text = pet.weightKg?.let { "${it} kg" } ?: "Profile ready")
    }
}

@Composable
private fun AddFirstPetCard(onClick: () -> Unit) {
    CareTailCard(modifier = Modifier.width(280.dp), backgroundColor = CareTailWarmSurface) {
        Text("No pets yet", style = MaterialTheme.typography.titleLarge, color = CareTailTextPrimary)
        Spacer(Modifier.height(6.dp))
        Text(
            "Create your first pet profile to track care reminders, health notes, and records.",
            style = MaterialTheme.typography.bodyMedium,
            color = CareTailTextSecondary,
        )
        Spacer(Modifier.height(14.dp))
        PrimaryCoralButton(text = "Add Pet", onClick = onClick)
    }
}

private fun petSubtitle(pet: PetEntity): String =
    listOfNotNull(pet.species, pet.breed).joinToString(" - ")

@Composable
private fun CareTaskCard(reminder: ReminderUiModel) {
    CareTailCard(backgroundColor = CareTailCardColor) {
        InfoRow(
            title = reminder.title,
            subtitle = "${reminder.petName} - ${reminder.dueTimeLabel}",
            icon = Icons.Rounded.AccessTime,
            trailing = {
                StatusPill(
                    text = reminder.type,
                    backgroundColor = CareTailAccentSoft,
                    contentColor = CareTailAccent,
                )
            },
        )
    }
}

@Composable
private fun LatestDiaryCard(entry: HealthDiaryEntryUiModel) {
    CareTailCard {
        Text("${entry.petName} - ${entry.dateLabel}", style = MaterialTheme.typography.labelLarge, color = CareTailTextSecondary)
        Spacer(Modifier.height(6.dp))
        StatusPill(text = entry.mood, contentColor = entry.moodColor)
        entry.notes?.let { notes ->
            Spacer(Modifier.height(8.dp))
            Text(notes, style = MaterialTheme.typography.bodyMedium, color = CareTailTextPrimary, maxLines = 2)
        } ?: entry.symptoms?.let { symptoms ->
            Spacer(Modifier.height(8.dp))
            Text(symptoms, style = MaterialTheme.typography.bodyMedium, color = CareTailTextPrimary, maxLines = 2)
        }
    }
}

@Composable
private fun UpcomingCard(reminder: ReminderUiModel) {
    CareTailCard {
        Text(reminder.title, style = MaterialTheme.typography.titleMedium, color = CareTailTextPrimary, fontWeight = FontWeight.SemiBold)
        Text(
            "${reminder.petName} - ${reminder.dueDateLabel} at ${reminder.dueTimeLabel}",
            style = MaterialTheme.typography.bodyMedium,
            color = CareTailTextSecondary,
        )
    }
}

@Composable
private fun EmptySmallCard(message: String) {
    CareTailCard {
        Text(message, style = MaterialTheme.typography.bodyLarge, color = CareTailTextSecondary)
    }
}
