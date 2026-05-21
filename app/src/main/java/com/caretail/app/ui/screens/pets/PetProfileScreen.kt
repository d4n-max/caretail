package com.caretail.app.ui.screens.pets

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
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.MonitorWeight
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
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.data.repository.ReminderRepository
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.CareTailScaffold
import com.caretail.app.ui.components.CareTailTopBar
import com.caretail.app.ui.components.PetAvatar
import com.caretail.app.ui.components.PrimaryCoralButton
import com.caretail.app.ui.components.SecondaryButton
import com.caretail.app.ui.components.SectionHeader
import com.caretail.app.ui.components.StatusPill
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
    petId: Long,
    onBack: () -> Unit,
    onAddReminder: (Long) -> Unit,
) {
    val factory = remember(petRepository, reminderRepository, petId) {
        PetProfileViewModelFactory(petRepository, reminderRepository, petId)
    }
    val viewModel: PetProfileViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val pet = uiState.pet

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
                    onAddReminder = { onAddReminder(pet.id) },
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
    onAddReminder: () -> Unit,
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
                PrimaryCoralButton(text = "Edit Profile", modifier = Modifier.weight(1f), onClick = {})
                SecondaryButton(text = "Share Records", modifier = Modifier.weight(1f), onClick = {})
            }
        }
    }
    Spacer(Modifier.height(22.dp))
    UpcomingRemindersSection(reminders = reminders, onAddReminder = onAddReminder)
    Spacer(Modifier.height(12.dp))
    EmptyProfileSection("Recent Health Diary", "Health notes will appear here.", Icons.Rounded.Favorite)
    Spacer(Modifier.height(12.dp))
    EmptyProfileSection("Documents & Records", "Documents will appear here.", Icons.Rounded.Description)
}

@Composable
private fun UpcomingRemindersSection(
    reminders: List<ReminderUiModel>,
    onAddReminder: () -> Unit,
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
            CareTailCard {
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
