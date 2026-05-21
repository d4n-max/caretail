package com.caretail.app.ui.screens.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.caretail.app.billing.PremiumUpsellReason
import com.caretail.app.data.repository.HealthDiaryRepository
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.CareTailScaffold
import com.caretail.app.ui.components.CareTailTopBar
import com.caretail.app.ui.components.PrimaryCoralButton
import com.caretail.app.ui.components.ReminderTypeChip
import com.caretail.app.ui.components.SectionHeader
import com.caretail.app.ui.components.careTailOutlinedTextFieldColors
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.theme.CareTailAccent
import com.caretail.app.ui.theme.CareTailAccentSoft
import com.caretail.app.ui.theme.CareTailTextSecondary
import com.caretail.app.ui.theme.CareTailWarmSurface
import com.caretail.app.ui.viewmodel.AddDiaryEntryViewModel
import com.caretail.app.ui.viewmodel.AddDiaryEntryViewModelFactory
import com.caretail.app.ui.viewmodel.AppetiteValues
import com.caretail.app.ui.viewmodel.EnergyLevelValues
import com.caretail.app.ui.viewmodel.MoodValues

@Composable
fun AddDiaryEntryScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    petRepository: PetRepository,
    healthDiaryRepository: HealthDiaryRepository,
    preselectedPetId: Long?,
    editEntryId: Long? = null,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onAddPet: () -> Unit,
    onOpenPremium: (PremiumUpsellReason) -> Unit,
) {
    val factory = remember(petRepository, healthDiaryRepository, preselectedPetId, editEntryId) {
        AddDiaryEntryViewModelFactory(petRepository, healthDiaryRepository, preselectedPetId, editEntryId)
    }
    val viewModel: AddDiaryEntryViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val textFieldColors = careTailOutlinedTextFieldColors()

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onSaved()
        }
    }
    LaunchedEffect(uiState.upsellReason) {
        uiState.upsellReason?.let(onOpenPremium)
    }

    CareTailScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        selectedBottomRoute = CareTailRoute.Diary.route,
        topBar = { CareTailTopBar(title = if (uiState.editingEntryId == null) "Add Health Note" else "Edit Health Note", showBack = true, onBack = onBack) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            if (!uiState.isLoading && uiState.pets.isEmpty()) {
                EmptyPetsState(onAddPet = onAddPet)
            } else {
                Text("Daily check-in", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Add wellbeing notes for your pet's care history.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = CareTailTextSecondary,
                )
                Spacer(Modifier.height(18.dp))
                CareTailCard {
                    SectionHeader("Pet")
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        uiState.pets.forEach { pet ->
                            ReminderTypeChip(
                                text = pet.name,
                                selected = uiState.selectedPetId == pet.id,
                                onClick = { viewModel.onPetSelected(pet.id) },
                            )
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                    Text("Mood", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(10.dp))
                    ChipRows(MoodValues, uiState.mood, viewModel::onMoodSelected)
                    Spacer(Modifier.height(18.dp))
                    Text("Appetite", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(10.dp))
                    ChipRows(AppetiteValues, uiState.appetite, viewModel::onAppetiteSelected)
                    Spacer(Modifier.height(18.dp))
                    Text("Energy", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(10.dp))
                    ChipRows(EnergyLevelValues, uiState.energyLevel, viewModel::onEnergySelected)
                    Spacer(Modifier.height(18.dp))
                    OutlinedTextField(
                        value = uiState.symptoms,
                        onValueChange = viewModel::onSymptomsChanged,
                        label = { Text("Symptoms") },
                        placeholder = { Text("Optional symptom journal notes") },
                        colors = textFieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(112.dp),
                    )
                    Spacer(Modifier.height(14.dp))
                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = viewModel::onNotesChanged,
                        label = { Text("Notes") },
                        placeholder = { Text("Daily wellbeing notes") },
                        colors = textFieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                    )
                    Spacer(Modifier.height(16.dp))
                    PhotoPlaceholder()
                }
                uiState.validationError?.let { message ->
                    Spacer(Modifier.height(12.dp))
                    Text(message, color = CareTailAccent, style = MaterialTheme.typography.bodyMedium)
                }
                uiState.generalError?.let { message ->
                    Spacer(Modifier.height(12.dp))
                    Text(message, color = CareTailAccent, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(24.dp))
                PrimaryCoralButton(
                    text = if (uiState.isLoading) "Saving..." else if (uiState.editingEntryId == null) "Save Entry" else "Save Changes",
                    onClick = viewModel::saveEntry,
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EmptyPetsState(onAddPet: () -> Unit) {
    CareTailCard(backgroundColor = CareTailWarmSurface) {
        Text("Add a pet first", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text(
            "Create a pet profile before adding health notes.",
            style = MaterialTheme.typography.bodyLarge,
            color = CareTailTextSecondary,
        )
        Spacer(Modifier.height(16.dp))
        PrimaryCoralButton(text = "Add Pet", onClick = onAddPet)
    }
}

@Composable
private fun ChipRows(
    values: List<String>,
    selectedValue: String,
    onSelected: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        values.chunked(3).forEach { rowValues ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowValues.forEach { value ->
                    ReminderTypeChip(
                        text = value,
                        selected = selectedValue == value,
                        onClick = { onSelected(value) },
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clip(MaterialTheme.shapes.large)
            .background(CareTailAccentSoft),
        contentAlignment = Alignment.Center,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.PhotoCamera, contentDescription = "Photo attachment", tint = CareTailAccent, modifier = Modifier.size(24.dp))
            Text("Photo attachment not available in this MVP", style = MaterialTheme.typography.bodyMedium, color = CareTailTextSecondary)
        }
    }
}
