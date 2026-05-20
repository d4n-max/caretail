package com.caretail.app.ui.screens.documents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.CareTailScaffold
import com.caretail.app.ui.components.CareTailTopBar
import com.caretail.app.ui.components.CoralFab
import com.caretail.app.ui.components.InfoRow
import com.caretail.app.ui.components.ReminderTypeChip
import com.caretail.app.ui.components.SectionHeader
import com.caretail.app.ui.components.StatusPill
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.theme.CareTailAccent
import com.caretail.app.ui.theme.CareTailAccentSoft
import com.caretail.app.ui.theme.CareTailTextPrimary
import com.caretail.app.ui.theme.CareTailTextSecondary

@Composable
fun DocumentsScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
) {
    CareTailScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        selectedBottomRoute = CareTailRoute.Settings.route,
        topBar = { CareTailTopBar(title = "Documents") },
        floatingActionButton = { CoralFab(onClick = {}) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text("Documents & Records", style = MaterialTheme.typography.headlineMedium, color = CareTailTextPrimary)
            Text("Vet records, invoices, and local document references.", style = MaterialTheme.typography.bodyLarge, color = CareTailTextSecondary)
            Spacer(Modifier.height(16.dp))
            ReminderTypeChip("Luna", selected = true)
            Spacer(Modifier.height(22.dp))
            SectionHeader("Luna")
            Spacer(Modifier.height(12.dp))
            CareTailCard(modifier = Modifier.fillMaxWidth()) {
                DocumentRow("Vaccination_Record_2023.pdf", "PDF - Added Oct 12, 2023", "Vaccine")
                Spacer(Modifier.height(12.dp))
                DocumentRow("Spay_Surgery_Invoice.pdf", "PDF - Added Jun 05, 2022", "Invoice")
                Spacer(Modifier.height(12.dp))
                DocumentRow("Microchip_Details.txt", "TXT - Added May 10, 2021", "Record")
            }
            Spacer(Modifier.height(90.dp))
        }
    }
}

@Composable
private fun DocumentRow(title: String, subtitle: String, type: String) {
    InfoRow(
        title = title,
        subtitle = subtitle,
        icon = Icons.Rounded.Description,
        trailing = {
            StatusPill(type, backgroundColor = CareTailAccentSoft, contentColor = CareTailAccent)
        },
    )
}
