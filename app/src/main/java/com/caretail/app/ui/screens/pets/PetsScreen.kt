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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.CareTailScaffold
import com.caretail.app.ui.components.CareTailTopBar
import com.caretail.app.ui.components.CoralFab
import com.caretail.app.ui.components.PetAvatar
import com.caretail.app.ui.components.SectionHeader
import com.caretail.app.ui.components.StatusPill
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.theme.CareTailAccentSoft
import com.caretail.app.ui.theme.CareTailPrimary
import com.caretail.app.ui.theme.CareTailPrimaryDark
import com.caretail.app.ui.theme.CareTailTextPrimary
import com.caretail.app.ui.theme.CareTailTextSecondary

@Composable
fun PetsScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onOpenPetProfile: () -> Unit,
    onAddPet: () -> Unit,
) {
    CareTailScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        selectedBottomRoute = CareTailRoute.Pets.route,
        topBar = { CareTailTopBar(title = "Pets") },
        floatingActionButton = { CoralFab(onClick = onAddPet) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            SectionHeader("Your Pets", actionText = "Add", onAction = onAddPet)
            Spacer(Modifier.height(14.dp))
            PetListCard(
                name = "Luna",
                subtitle = "Cat - British Shorthair",
                status = "Vaccinated",
                next = "Rabies Booster tomorrow at 10:00 AM",
                onClick = onOpenPetProfile,
            )
            Spacer(Modifier.height(12.dp))
            PetListCard(
                name = "Max",
                subtitle = "Dog - Golden Retriever",
                status = "Good Appetite",
                next = "Heartworm Meds today at 8:00 PM",
                onClick = onOpenPetProfile,
            )
            Spacer(Modifier.height(90.dp))
        }
    }
}

@Composable
private fun PetListCard(
    name: String,
    subtitle: String,
    status: String,
    next: String,
    onClick: () -> Unit,
) {
    CareTailCard(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            PetAvatar(name = name, backgroundColor = CareTailPrimary.copy(alpha = 0.18f))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.titleLarge, color = CareTailTextPrimary)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = CareTailTextSecondary)
                Spacer(Modifier.height(10.dp))
                StatusPill(
                    text = status,
                    backgroundColor = if (status == "Vaccinated") CareTailPrimary.copy(alpha = 0.16f) else CareTailAccentSoft,
                    contentColor = if (status == "Vaccinated") CareTailPrimaryDark else CareTailTextPrimary,
                )
                Spacer(Modifier.height(10.dp))
                Text("Next: $next", style = MaterialTheme.typography.bodyMedium, color = CareTailTextSecondary)
            }
        }
    }
}
