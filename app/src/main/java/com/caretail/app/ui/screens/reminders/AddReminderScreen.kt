package com.caretail.app.ui.screens.reminders

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
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.CareTailScaffold
import com.caretail.app.ui.components.CareTailTopBar
import com.caretail.app.ui.components.InfoRow
import com.caretail.app.ui.components.PrimaryCoralButton
import com.caretail.app.ui.components.ReminderTypeChip
import com.caretail.app.ui.components.SectionHeader
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.theme.CareTailTextSecondary

@Composable
fun AddReminderScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
) {
    CareTailScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        selectedBottomRoute = CareTailRoute.Reminders.route,
        topBar = { CareTailTopBar(title = "Add New Reminder", showBack = true, onBack = onBack) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            SectionHeader("Who is this for?")
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ReminderTypeChip("Luna", selected = true)
                ReminderTypeChip("Max", selected = false)
            }
            Spacer(Modifier.height(26.dp))
            SectionHeader("What type of reminder?")
            Spacer(Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ReminderTypeChip("Vaccine", selected = true)
                    ReminderTypeChip("Medication", selected = false)
                    ReminderTypeChip("Vet Visit", selected = false)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ReminderTypeChip("Grooming", selected = false)
                    ReminderTypeChip("Food", selected = false)
                    ReminderTypeChip("Other", selected = false)
                }
            }
            Spacer(Modifier.height(26.dp))
            CareTailCard {
                InfoRow("Date", "Oct 24, 2026", Icons.Rounded.CalendarToday)
                Spacer(Modifier.height(16.dp))
                InfoRow("Time", "09:00 AM", Icons.Rounded.Schedule)
                Spacer(Modifier.height(16.dp))
                InfoRow("Repeat", "Never", Icons.Rounded.Repeat)
            }
            Spacer(Modifier.height(18.dp))
            Text("Notes", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Add any details here...", color = CareTailTextSecondary) },
                leadingIcon = { androidx.compose.material3.Icon(Icons.Rounded.Notes, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
            )
            Spacer(Modifier.height(24.dp))
            PrimaryCoralButton(text = "Save Reminder", onClick = onBack)
            Spacer(Modifier.height(18.dp))
        }
    }
}
