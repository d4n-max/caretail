package com.caretail.app.ui.screens.reminders

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Vaccines
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
import com.caretail.app.ui.components.InfoRow
import com.caretail.app.ui.components.SectionHeader
import com.caretail.app.ui.components.StatusPill
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.theme.CareTailAccent
import com.caretail.app.ui.theme.CareTailAccentSoft
import com.caretail.app.ui.theme.CareTailPrimary
import com.caretail.app.ui.theme.CareTailPrimaryDark
import com.caretail.app.ui.theme.CareTailTextPrimary
import com.caretail.app.ui.theme.CareTailTextSecondary

@Composable
fun RemindersScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onAddReminder: () -> Unit,
) {
    CareTailScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        selectedBottomRoute = CareTailRoute.Reminders.route,
        topBar = { CareTailTopBar(title = "Reminders") },
        floatingActionButton = { CoralFab(onClick = onAddReminder) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            SectionHeader("Today")
            Spacer(Modifier.height(12.dp))
            ReminderCard("Heartworm Meds", "Max - 8:00 PM", "Medication", false, Icons.Rounded.Medication)
            Spacer(Modifier.height(10.dp))
            ReminderCard("Evening Walk", "Max - 5:00 PM", "Food", false, Icons.Rounded.Pets)
            Spacer(Modifier.height(24.dp))
            SectionHeader("Upcoming")
            Spacer(Modifier.height(12.dp))
            ReminderCard("Rabies Booster", "Luna - tomorrow at 10:00 AM", "Vaccine", false, Icons.Rounded.Vaccines)
            Spacer(Modifier.height(10.dp))
            ReminderCard("Grooming Session", "Max - Oct 12", "Grooming", false, Icons.Rounded.Pets)
            Spacer(Modifier.height(24.dp))
            SectionHeader("Completed")
            Spacer(Modifier.height(12.dp))
            ReminderCard("Morning Feeding", "Luna - 7:30 AM", "Food", true, Icons.Rounded.Check)
            Spacer(Modifier.height(90.dp))
        }
    }
}

@Composable
private fun ReminderCard(
    title: String,
    subtitle: String,
    type: String,
    complete: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    CareTailCard(modifier = Modifier.fillMaxWidth()) {
        InfoRow(
            title = title,
            subtitle = subtitle,
            icon = icon,
            trailing = {
                StatusPill(
                    text = if (complete) "Done" else type,
                    backgroundColor = if (complete) CareTailPrimary.copy(alpha = 0.16f) else CareTailAccentSoft,
                    contentColor = if (complete) CareTailPrimaryDark else CareTailAccent,
                )
            },
        )
        if (!complete) {
            Spacer(Modifier.height(10.dp))
            Text("Care reminder", style = MaterialTheme.typography.bodyMedium, color = CareTailTextSecondary)
        }
    }
}
