package com.caretail.app.ui.screens.home

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.theme.CareTailAccent
import com.caretail.app.ui.theme.CareTailAccentSoft
import com.caretail.app.ui.theme.CareTailCard as CareTailCardColor
import com.caretail.app.ui.theme.CareTailPrimary
import com.caretail.app.ui.theme.CareTailPrimaryDark
import com.caretail.app.ui.theme.CareTailTextPrimary
import com.caretail.app.ui.theme.CareTailTextSecondary
import com.caretail.app.ui.theme.CareTailWarmSurface

@Composable
fun HomeScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onOpenPremium: () -> Unit,
    onAddReminder: () -> Unit,
) {
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
            Text("Good morning, Sarah!", style = MaterialTheme.typography.headlineMedium, color = CareTailTextPrimary)
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
                PetSummaryCard("Luna", "Cat", "British Shorthair", "Vaccinated", CareTailPrimary.copy(alpha = 0.22f))
                PetSummaryCard("Max", "Dog", "Golden Retriever", "Walk at 5 PM", CareTailWarmSurface)
            }
            Spacer(Modifier.height(24.dp))
            SectionHeader("Quick Actions")
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionCard("Add Pet", AddPetIcon, Modifier.weight(1f), onClick = { onNavigate(CareTailRoute.Pets.route) })
                QuickActionCard("Reminder", ReminderIcon, Modifier.weight(1f), onClick = onAddReminder)
                QuickActionCard("Log Health", HealthIcon, Modifier.weight(1f), onClick = { onNavigate(CareTailRoute.Diary.route) })
                QuickActionCard("Document", DocumentIcon, Modifier.weight(1f), onClick = { onNavigate(CareTailRoute.Settings.route) })
            }
            Spacer(Modifier.height(24.dp))
            SectionHeader("Today's Care", icon = Icons.Rounded.Event)
            Spacer(Modifier.height(12.dp))
            CareTaskCard("Heartworm Meds", "Max - 8:00 PM", true)
            Spacer(Modifier.height(10.dp))
            CareTaskCard("Evening Walk", "Max - 5:00 PM", false)
            Spacer(Modifier.height(24.dp))
            SectionHeader("Upcoming")
            Spacer(Modifier.height(12.dp))
            UpcomingCard("Rabies Booster", "Luna - tomorrow at 10:00 AM")
            Spacer(Modifier.height(10.dp))
            UpcomingCard("Grooming Session", "Max - Oct 12")
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
    name: String,
    species: String,
    breed: String,
    status: String,
    avatarColor: androidx.compose.ui.graphics.Color,
) {
    CareTailCard(modifier = Modifier.width(248.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            PetAvatar(name = name, size = 62.dp, backgroundColor = avatarColor)
            Column {
                Text(name, style = MaterialTheme.typography.titleLarge, color = CareTailTextPrimary)
                Text("$species - $breed", style = MaterialTheme.typography.bodyMedium, color = CareTailTextSecondary)
            }
        }
        Spacer(Modifier.height(14.dp))
        StatusPill(text = status)
    }
}

@Composable
private fun CareTaskCard(title: String, subtitle: String, complete: Boolean) {
    CareTailCard(backgroundColor = CareTailCardColor) {
        InfoRow(
            title = title,
            subtitle = subtitle,
            icon = Icons.Rounded.AccessTime,
            trailing = {
                StatusPill(
                    text = if (complete) "Done" else "Due",
                    backgroundColor = if (complete) CareTailPrimary.copy(alpha = 0.18f) else CareTailAccentSoft,
                    contentColor = if (complete) CareTailPrimaryDark else CareTailAccent,
                )
            },
        )
    }
}

@Composable
private fun UpcomingCard(title: String, subtitle: String) {
    CareTailCard {
        Text(title, style = MaterialTheme.typography.titleMedium, color = CareTailTextPrimary, fontWeight = FontWeight.SemiBold)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = CareTailTextSecondary)
    }
}
