package com.caretail.app.ui.screens.diary

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.caretail.app.data.repository.HealthDiaryRepository
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.CareTailScaffold
import com.caretail.app.ui.components.CareTailTopBar
import com.caretail.app.ui.components.CoralFab
import com.caretail.app.ui.components.PrimaryCoralButton
import com.caretail.app.ui.components.ReminderTypeChip
import com.caretail.app.ui.components.SectionHeader
import com.caretail.app.ui.components.StatusPill
import com.caretail.app.ui.model.HealthDiaryEntryUiModel
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.theme.CareTailAccent
import com.caretail.app.ui.theme.CareTailAccentSoft
import com.caretail.app.ui.theme.CareTailBlue
import com.caretail.app.ui.theme.CareTailPrimary
import com.caretail.app.ui.theme.CareTailPrimaryDark
import com.caretail.app.ui.theme.CareTailTextPrimary
import com.caretail.app.ui.theme.CareTailTextSecondary
import com.caretail.app.ui.theme.CareTailWarmSurface
import com.caretail.app.ui.viewmodel.HealthDiaryViewModel
import com.caretail.app.ui.viewmodel.HealthDiaryViewModelFactory

@Composable
fun HealthDiaryScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    petRepository: PetRepository,
    healthDiaryRepository: HealthDiaryRepository,
    onAddDiaryEntry: () -> Unit,
    onAddPet: () -> Unit,
) {
    val factory = remember(petRepository, healthDiaryRepository) {
        HealthDiaryViewModelFactory(petRepository, healthDiaryRepository)
    }
    val viewModel: HealthDiaryViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    CareTailScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        selectedBottomRoute = CareTailRoute.Diary.route,
        topBar = {
            CareTailTopBar(
                title = "CareTail",
                showAvatar = true,
                showSettings = true,
                onSettings = { onNavigate(CareTailRoute.Settings.route) },
            )
        },
        floatingActionButton = { CoralFab(onClick = onAddDiaryEntry) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text("Health Diary", style = MaterialTheme.typography.headlineMedium, color = CareTailTextPrimary)
            Text(
                "Track daily wellbeing notes for your pets.",
                style = MaterialTheme.typography.bodyLarge,
                color = CareTailTextSecondary,
            )
            Spacer(Modifier.height(16.dp))
            when {
                !uiState.isLoading && uiState.pets.isEmpty() -> EmptyPetsState(onAddPet = onAddPet)
                !uiState.isLoading -> {
                    if (!uiState.isPremium) {
                        CareTailCard(backgroundColor = CareTailWarmSurface) {
                            Text(
                                "Free health notes: ${uiState.totalEntryCount}/${uiState.freeEntryLimit}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = CareTailTextSecondary,
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                    }
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        uiState.pets.forEach { pet ->
                            ReminderTypeChip(
                                text = pet.name,
                                selected = uiState.selectedPetId == pet.id,
                                onClick = { viewModel.onPetSelected(pet.id) },
                            )
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    if (!uiState.hasEntries) {
                        EmptyEntriesState(onAddDiaryEntry = onAddDiaryEntry)
                    } else {
                        uiState.groupedEntries.forEach { (dateLabel, entries) ->
                            SectionHeader(dateLabel)
                            Spacer(Modifier.height(12.dp))
                            entries.forEach { entry ->
                                DiaryEntryCard(entry = entry, onDelete = { viewModel.deleteEntry(entry) })
                                Spacer(Modifier.height(10.dp))
                            }
                            Spacer(Modifier.height(14.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.height(90.dp))
        }
    }
}

@Composable
private fun EmptyPetsState(onAddPet: () -> Unit) {
    CareTailCard(backgroundColor = CareTailWarmSurface) {
        Text("Add a pet first", style = MaterialTheme.typography.titleLarge, color = CareTailTextPrimary)
        Spacer(Modifier.height(8.dp))
        Text("Create a pet profile before adding health notes.", style = MaterialTheme.typography.bodyLarge, color = CareTailTextSecondary)
        Spacer(Modifier.height(16.dp))
        PrimaryCoralButton(text = "Add Pet", onClick = onAddPet)
    }
}

@Composable
private fun EmptyEntriesState(onAddDiaryEntry: () -> Unit) {
    CareTailCard(backgroundColor = CareTailWarmSurface) {
        Text("No health notes yet", style = MaterialTheme.typography.titleLarge, color = CareTailTextPrimary)
        Spacer(Modifier.height(8.dp))
        Text("Start building a simple care history for this pet.", style = MaterialTheme.typography.bodyLarge, color = CareTailTextSecondary)
        Spacer(Modifier.height(16.dp))
        PrimaryCoralButton(text = "Add Entry", onClick = onAddDiaryEntry)
    }
}

@Composable
private fun DiaryEntryCard(
    entry: HealthDiaryEntryUiModel,
    onDelete: () -> Unit,
) {
    CareTailCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(entry.petName.uppercase(), style = MaterialTheme.typography.labelLarge, color = CareTailTextSecondary)
            Row {
                Text(entry.timeLabel, style = MaterialTheme.typography.bodyMedium, color = CareTailTextPrimary)
                IconButton(onClick = onDelete) {
                    Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = CareTailTextSecondary)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusPill(text = entry.mood, icon = Icons.Rounded.Mood, backgroundColor = CareTailAccentSoft, contentColor = entry.moodColor)
            StatusPill(text = entry.appetite, icon = Icons.Rounded.Fastfood, backgroundColor = CareTailPrimary.copy(alpha = 0.15f), contentColor = CareTailPrimaryDark)
            StatusPill(text = entry.energyLevel, icon = Icons.Rounded.Bolt, backgroundColor = CareTailAccentSoft, contentColor = CareTailBlue)
        }
        entry.symptoms?.let { symptoms ->
            Spacer(Modifier.height(12.dp))
            Text("Symptoms", style = MaterialTheme.typography.labelLarge, color = CareTailTextSecondary)
            Text(
                symptoms,
                style = MaterialTheme.typography.bodyMedium,
                color = CareTailTextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        entry.notes?.let { notes ->
            Spacer(Modifier.height(12.dp))
            Text(
                notes,
                style = MaterialTheme.typography.bodyLarge,
                color = CareTailTextPrimary,
                fontWeight = FontWeight.Normal,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
