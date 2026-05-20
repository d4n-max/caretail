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
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.MonitorWeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.CareTailScaffold
import com.caretail.app.ui.components.CareTailTopBar
import com.caretail.app.ui.components.InfoRow
import com.caretail.app.ui.components.PetAvatar
import com.caretail.app.ui.components.PrimaryCoralButton
import com.caretail.app.ui.components.SecondaryButton
import com.caretail.app.ui.components.SectionHeader
import com.caretail.app.ui.components.StatusPill
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.theme.CareTailAccent
import com.caretail.app.ui.theme.CareTailAccentSoft
import com.caretail.app.ui.theme.CareTailPrimary
import com.caretail.app.ui.theme.CareTailPrimaryDark
import com.caretail.app.ui.theme.CareTailTextPrimary
import com.caretail.app.ui.theme.CareTailTextSecondary
import com.caretail.app.ui.theme.CareTailWarmSurface

@Composable
fun PetProfileScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
) {
    CareTailScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        selectedBottomRoute = CareTailRoute.Pets.route,
        topBar = {
            CareTailTopBar(
                title = "Luna's Profile",
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
            CareTailCard(modifier = Modifier.fillMaxWidth(), backgroundColor = CareTailWarmSurface) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    PetAvatar(name = "Luna", size = 108.dp, backgroundColor = CareTailPrimary.copy(alpha = 0.18f))
                    Spacer(Modifier.height(16.dp))
                    Text("Luna", style = MaterialTheme.typography.headlineMedium, color = CareTailTextPrimary)
                    Text("British Shorthair", style = MaterialTheme.typography.bodyLarge, color = CareTailTextSecondary)
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatusPill("3 years", backgroundColor = CareTailAccentSoft, contentColor = CareTailTextPrimary, icon = Icons.Rounded.Cake)
                        StatusPill("4.2 kg", backgroundColor = CareTailAccentSoft, contentColor = CareTailTextPrimary, icon = Icons.Rounded.MonitorWeight)
                    }
                    Spacer(Modifier.height(12.dp))
                    StatusPill("Vaccinated", backgroundColor = CareTailPrimary.copy(alpha = 0.18f), contentColor = CareTailPrimaryDark)
                    Spacer(Modifier.height(18.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PrimaryCoralButton(text = "Edit Profile", modifier = Modifier.weight(1f), onClick = {})
                        SecondaryButton(text = "Share Records", modifier = Modifier.weight(1f), onClick = {})
                    }
                }
            }
            Spacer(Modifier.height(22.dp))
            ProfileSection("Upcoming Reminders", "Annual Booster Vaccine", "Due tomorrow - Dr. Smith at City Vet Clinic", Icons.Rounded.Event)
            Spacer(Modifier.height(12.dp))
            ProfileSection("Recent Health Diary", "Happy & Playful", "Good appetite - Normal energy - Logged today", Icons.Rounded.Favorite)
            Spacer(Modifier.height(12.dp))
            SectionHeader("Documents & Records", icon = Icons.Rounded.Description)
            Spacer(Modifier.height(12.dp))
            CareTailCard {
                InfoRow("Vaccination_Record_2023.pdf", "Added Oct 12, 2023 - 1.2 MB", Icons.Rounded.Description)
                Spacer(Modifier.height(10.dp))
                InfoRow("Microchip_Details.txt", "Added May 10, 2021 - 12 KB", Icons.Rounded.Description)
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ProfileSection(title: String, primary: String, secondary: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    SectionHeader(title, icon = icon)
    Spacer(Modifier.height(12.dp))
    CareTailCard {
        Text(primary, style = MaterialTheme.typography.titleMedium, color = CareTailTextPrimary, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text(secondary, style = MaterialTheme.typography.bodyMedium, color = CareTailTextSecondary)
    }
}
