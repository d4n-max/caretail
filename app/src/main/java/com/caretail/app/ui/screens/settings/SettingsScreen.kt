package com.caretail.app.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.CareTailScaffold
import com.caretail.app.ui.components.CareTailTopBar
import com.caretail.app.ui.components.InfoRow
import com.caretail.app.ui.components.SectionHeader
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.theme.CareTailAccent
import com.caretail.app.ui.theme.CareTailTextPrimary
import com.caretail.app.ui.theme.CareTailTextSecondary

@Composable
fun SettingsScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onOpenPremium: () -> Unit,
    onOpenDocuments: () -> Unit,
) {
    CareTailScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        selectedBottomRoute = CareTailRoute.Settings.route,
        topBar = { CareTailTopBar(title = "Settings") },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text("Settings", style = MaterialTheme.typography.headlineMedium, color = CareTailTextPrimary)
            Text("CareTail is local-first for MVP.", style = MaterialTheme.typography.bodyLarge, color = CareTailTextSecondary)
            Spacer(Modifier.height(22.dp))
            SectionHeader("Premium")
            Spacer(Modifier.height(10.dp))
            CareTailCard(modifier = Modifier.fillMaxWidth()) {
                InfoRow("Manage Premium", "Review Premium benefits", Icons.Rounded.Star)
            }
            Spacer(Modifier.height(20.dp))
            SectionHeader("Privacy")
            Spacer(Modifier.height(10.dp))
            CareTailCard(modifier = Modifier.fillMaxWidth()) {
                InfoRow("Privacy Policy", "Placeholder for future policy", Icons.Rounded.PrivacyTip)
                Spacer(Modifier.height(12.dp))
                InfoRow("No cloud sync", "MVP data stays on this device", Icons.Rounded.CloudOff)
            }
            Spacer(Modifier.height(20.dp))
            SectionHeader("Data")
            Spacer(Modifier.height(10.dp))
            CareTailCard(modifier = Modifier.fillMaxWidth()) {
                InfoRow("Export Data", "Future local export placeholder", Icons.Rounded.UploadFile)
                Spacer(Modifier.height(12.dp))
                InfoRow("Delete Local Data", "Placeholder only", Icons.Rounded.DeleteOutline)
            }
            Spacer(Modifier.height(20.dp))
            SectionHeader("About")
            Spacer(Modifier.height(10.dp))
            CareTailCard(modifier = Modifier.fillMaxWidth()) {
                InfoRow("App Version", "0.1.0", Icons.Rounded.Info)
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}
