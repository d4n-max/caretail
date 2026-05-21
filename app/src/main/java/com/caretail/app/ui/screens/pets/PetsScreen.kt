package com.caretail.app.ui.screens.pets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.CareTailScaffold
import com.caretail.app.ui.components.CareTailTopBar
import com.caretail.app.ui.components.CoralFab
import com.caretail.app.ui.components.EmptyStateCard
import com.caretail.app.ui.components.PetAvatar
import com.caretail.app.ui.components.PrimaryCoralButton
import com.caretail.app.ui.components.SectionHeader
import com.caretail.app.ui.components.StatusPill
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.theme.CareTailAccentSoft
import com.caretail.app.ui.theme.CareTailPrimary
import com.caretail.app.ui.theme.CareTailPrimaryDark
import com.caretail.app.ui.theme.CareTailTextPrimary
import com.caretail.app.ui.theme.CareTailTextSecondary
import com.caretail.app.ui.theme.CareTailWarmSurface
import com.caretail.app.ui.viewmodel.PetsViewModel
import com.caretail.app.ui.viewmodel.PetsViewModelFactory

@Composable
fun PetsScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    petRepository: PetRepository,
    onOpenPetProfile: (Long) -> Unit,
    onAddPet: () -> Unit,
    onOpenPremium: () -> Unit,
) {
    val factory = remember(petRepository) { PetsViewModelFactory(petRepository) }
    val viewModel: PetsViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val addPetAction = { if (uiState.canAddPet) onAddPet() else onOpenPremium() }

    CareTailScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        selectedBottomRoute = CareTailRoute.Pets.route,
        topBar = { CareTailTopBar(title = "Pets") },
        floatingActionButton = { CoralFab(onClick = addPetAction, contentDescription = "Add Pet") },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            SectionHeader("Your Pets", actionText = "Add", onAction = addPetAction)
            Spacer(Modifier.height(14.dp))
            if (uiState.pets.isEmpty()) {
                EmptyPetsCard(onAddPet = onAddPet)
            } else {
                uiState.pets.forEach { pet ->
                    PetListCard(
                        pet = pet,
                        onClick = { onOpenPetProfile(pet.id) },
                    )
                    Spacer(Modifier.height(12.dp))
                }
                if (!uiState.canAddPet) {
                    PremiumLimitCard(onOpenPremium = onOpenPremium)
                }
            }
            Spacer(Modifier.height(90.dp))
        }
    }
}

@Composable
private fun EmptyPetsCard(onAddPet: () -> Unit) {
    EmptyStateCard(
        title = "No pets yet",
        message = "Create your first pet profile to track care reminders, health notes, and records.",
        actionText = "Add Pet",
        onAction = onAddPet,
    )
}

@Composable
private fun PremiumLimitCard(onOpenPremium: () -> Unit) {
    CareTailCard(modifier = Modifier.fillMaxWidth(), backgroundColor = CareTailWarmSurface) {
        Text("Need more pets?", style = MaterialTheme.typography.titleMedium, color = CareTailTextPrimary)
        Text(
            "Free CareTail includes 1 pet. Premium unlocks unlimited pet profiles.",
            style = MaterialTheme.typography.bodyMedium,
            color = CareTailTextSecondary,
        )
        Spacer(Modifier.height(14.dp))
        PrimaryCoralButton(text = "View Premium", onClick = onOpenPremium)
    }
}

@Composable
private fun PetListCard(
    pet: PetEntity,
    onClick: () -> Unit,
) {
    CareTailCard(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            PetAvatar(name = pet.name, backgroundColor = CareTailPrimary.copy(alpha = 0.18f))
            Column(modifier = Modifier.weight(1f)) {
                Text(pet.name, style = MaterialTheme.typography.titleLarge, color = CareTailTextPrimary)
                Text(
                    listOfNotNull(pet.species, pet.breed).joinToString(" - "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = CareTailTextSecondary,
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusPill(
                        text = pet.species,
                        backgroundColor = CareTailPrimary.copy(alpha = 0.16f),
                        contentColor = CareTailPrimaryDark,
                    )
                    pet.weightKg?.let { weight ->
                        StatusPill(
                            text = "${weight} kg",
                            backgroundColor = CareTailAccentSoft,
                            contentColor = CareTailTextPrimary,
                        )
                    }
                }
                pet.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                    Spacer(Modifier.height(10.dp))
                    Text(
                        notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CareTailTextSecondary,
                        maxLines = 2,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
        }
    }
}
