package com.caretail.app.ui.screens.diary

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
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.CareTailScaffold
import com.caretail.app.ui.components.CareTailTopBar
import com.caretail.app.ui.components.CoralFab
import com.caretail.app.ui.components.DropDownIcon
import com.caretail.app.ui.components.ReminderTypeChip
import com.caretail.app.ui.components.SectionHeader
import com.caretail.app.ui.components.StatusPill
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.theme.CareTailAccent
import com.caretail.app.ui.theme.CareTailAccentSoft
import com.caretail.app.ui.theme.CareTailBlue
import com.caretail.app.ui.theme.CareTailPrimary
import com.caretail.app.ui.theme.CareTailPrimaryDark
import com.caretail.app.ui.theme.CareTailTextPrimary
import com.caretail.app.ui.theme.CareTailTextSecondary

@Composable
fun HealthDiaryScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onAddDiaryEntry: () -> Unit,
) {
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
            Text("Track Luna's daily wellbeing.", style = MaterialTheme.typography.bodyLarge, color = CareTailTextSecondary)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ReminderTypeChip("Luna", selected = true)
                StatusPill("All entries", icon = DropDownIcon)
            }
            Spacer(Modifier.height(24.dp))
            SectionHeader("Today, Oct 24")
            Spacer(Modifier.height(12.dp))
            DiaryEntryCard(
                title = "Morning Routine",
                time = "08:30 AM",
                notes = "Luna seems a bit tired today, but ate all her breakfast. We skipped the long morning run for a gentle stroll.",
                chips = listOf("Content", "Good Appetite", "Low Energy"),
            )
            Spacer(Modifier.height(24.dp))
            SectionHeader("Yesterday, Oct 23")
            Spacer(Modifier.height(12.dp))
            DiaryEntryCard(
                title = "Evening Notes",
                time = "07:15 PM",
                notes = "Very playful at the park today. Drank plenty of water afterwards.",
                chips = listOf("Very Happy", "High Energy"),
            )
            Spacer(Modifier.height(90.dp))
        }
    }
}

@Composable
private fun DiaryEntryCard(
    title: String,
    time: String,
    notes: String,
    chips: List<String>,
) {
    CareTailCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title.uppercase(), style = MaterialTheme.typography.labelLarge, color = CareTailTextSecondary)
            Text(time, style = MaterialTheme.typography.bodyMedium, color = CareTailTextPrimary)
        }
        Spacer(Modifier.height(12.dp))
        Text(notes, style = MaterialTheme.typography.bodyLarge, color = CareTailTextPrimary, fontWeight = FontWeight.Normal)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            chips.forEach { chip ->
                val icon = when {
                    chip.contains("Appetite") -> Icons.Rounded.Fastfood
                    chip.contains("Energy") -> Icons.Rounded.Bolt
                    else -> Icons.Rounded.Mood
                }
                val color = when {
                    chip.contains("Energy") -> CareTailBlue
                    chip.contains("Appetite") -> CareTailPrimaryDark
                    else -> CareTailAccent
                }
                StatusPill(
                    text = chip,
                    icon = icon,
                    backgroundColor = if (chip.contains("Appetite")) CareTailPrimary.copy(alpha = 0.15f) else CareTailAccentSoft,
                    contentColor = color,
                )
            }
        }
    }
}
