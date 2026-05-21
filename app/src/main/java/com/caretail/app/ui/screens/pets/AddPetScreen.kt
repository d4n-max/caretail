package com.caretail.app.ui.screens.pets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.CareTailScaffold
import com.caretail.app.ui.components.CareTailTopBar
import com.caretail.app.ui.components.PetImagePlaceholder
import com.caretail.app.ui.components.PrimaryCoralButton
import com.caretail.app.ui.components.ReminderTypeChip
import com.caretail.app.ui.components.careTailOutlinedTextFieldColors
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.theme.CareTailAccent
import com.caretail.app.ui.theme.CareTailTextSecondary
import com.caretail.app.ui.viewmodel.AddPetViewModel
import com.caretail.app.ui.viewmodel.AddPetViewModelFactory

@Composable
fun AddPetScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    petRepository: PetRepository,
    onBack: () -> Unit,
    onSaved: (Long) -> Unit,
    onOpenPremium: () -> Unit,
) {
    val factory = remember(petRepository) { AddPetViewModelFactory(petRepository) }
    val viewModel: AddPetViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val textFieldColors = careTailOutlinedTextFieldColors()
    val validationMessage = uiState.validationError.orEmpty()

    LaunchedEffect(uiState.savedPetId) {
        uiState.savedPetId?.let(onSaved)
    }

    if (uiState.showPremiumUpsell) {
        AlertDialog(
            onDismissRequest = viewModel::clearPremiumUpsell,
            title = { Text("CareTail Premium") },
            text = { Text("Free users can add 1 pet. Unlock Premium for unlimited pet profiles.") },
            confirmButton = {
                TextButton(onClick = onOpenPremium) {
                    Text("View Premium")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::clearPremiumUpsell) {
                    Text("Not now")
                }
            },
        )
    }

    CareTailScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        selectedBottomRoute = CareTailRoute.Pets.route,
        topBar = { CareTailTopBar(title = "Add Pet", showBack = true, onBack = onBack) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text("Pet details", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Create a local profile to track care reminders, health notes, and records.",
                style = MaterialTheme.typography.bodyLarge,
                color = CareTailTextSecondary,
            )
            Spacer(Modifier.height(18.dp))
            PetImagePlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
            )
            Spacer(Modifier.height(18.dp))
            CareTailCard {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::onNameChanged,
                    label = { Text("Name *") },
                    singleLine = true,
                    isError = validationMessage.contains("name", ignoreCase = true),
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(14.dp))
                Text("Species *", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("Cat", "Dog", "Other").forEach { species ->
                        ReminderTypeChip(
                            text = species,
                            selected = uiState.species == species,
                            onClick = { viewModel.onSpeciesSelected(species) },
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = uiState.breed,
                    onValueChange = viewModel::onBreedChanged,
                    label = { Text("Breed") },
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = uiState.gender,
                    onValueChange = viewModel::onGenderChanged,
                    label = { Text("Gender") },
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = uiState.birthDateText,
                    onValueChange = viewModel::onBirthDateChanged,
                    label = { Text("Birth date") },
                    placeholder = { Text("Optional for now", color = CareTailTextSecondary.copy(alpha = 0.75f)) },
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = uiState.weightKg,
                    onValueChange = viewModel::onWeightChanged,
                    label = { Text("Weight kg") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = validationMessage.contains("weight", ignoreCase = true),
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = uiState.notes,
                    onValueChange = viewModel::onNotesChanged,
                    label = { Text("Notes") },
                    colors = textFieldColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                )
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
                text = if (uiState.isLoading) "Saving..." else "Save Pet",
                onClick = viewModel::savePet,
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
