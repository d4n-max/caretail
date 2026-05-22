package com.caretail.app.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.caretail.app.BuildConfig
import com.caretail.app.billing.PremiumManager
import com.caretail.app.data.repository.HealthDiaryRepository
import com.caretail.app.data.repository.PetDocumentRepository
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.data.repository.ReminderRepository
import com.caretail.app.reminders.ReminderNotificationScheduler
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.CareTailScaffold
import com.caretail.app.ui.components.CareTailTopBar
import com.caretail.app.ui.components.DestructiveCareTailButton
import com.caretail.app.ui.components.TextActionButton
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.theme.CareTailAccent
import com.caretail.app.ui.theme.CareTailAccentSoft
import com.caretail.app.ui.theme.CareTailPrimary
import com.caretail.app.ui.theme.CareTailPrimaryDark
import com.caretail.app.ui.theme.CareTailTextPrimary
import com.caretail.app.ui.theme.CareTailTextSecondary
import com.caretail.app.ui.theme.CareTailWarmSurface
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onOpenPremium: () -> Unit,
    onOpenDocuments: () -> Unit,
    petRepository: PetRepository,
    reminderRepository: ReminderRepository,
    healthDiaryRepository: HealthDiaryRepository,
    petDocumentRepository: PetDocumentRepository,
    reminderNotificationScheduler: ReminderNotificationScheduler,
    onLocalDataDeleted: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isPremium by PremiumManager.isPremium.collectAsState()
    val isPremiumTestMode by PremiumManager.isPremiumTestMode.collectAsState()
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    feedbackMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { feedbackMessage = null },
            confirmButton = {
                TextActionButton(text = "OK", onClick = { feedbackMessage = null })
            },
            title = { Text("CareTail") },
            text = { Text(message) },
        )
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { if (!isDeleting) showDeleteConfirmation = false },
            title = { Text("Delete all local data?") },
            text = {
                Text(
                    "This will remove all pet profiles, reminders, health notes, and document records stored on this device. This cannot be undone.",
                )
            },
            confirmButton = {
                DestructiveCareTailButton(
                    text = if (isDeleting) "Deleting..." else "Delete",
                    enabled = !isDeleting,
                    onClick = {
                        scope.launch {
                            isDeleting = true
                            try {
                                reminderRepository.getAllReminders().forEach { reminder ->
                                    reminderNotificationScheduler.cancelReminder(reminder.id)
                                }
                                petDocumentRepository.deleteAllDocuments()
                                healthDiaryRepository.deleteAllEntries()
                                reminderRepository.deleteAllReminders()
                                petRepository.deleteAllPets()
                                showDeleteConfirmation = false
                                Toast.makeText(context, "Local data deleted.", Toast.LENGTH_SHORT).show()
                                onLocalDataDeleted()
                            } catch (error: Exception) {
                                feedbackMessage = "Local data could not be deleted. Please try again."
                            } finally {
                                isDeleting = false
                            }
                        }
                    },
                )
            },
            dismissButton = {
                TextActionButton(
                    text = "Cancel",
                    enabled = !isDeleting,
                    onClick = { showDeleteConfirmation = false },
                )
            },
        )
    }

    CareTailScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        selectedBottomRoute = CareTailRoute.Settings.route,
        topBar = { CareTailTopBar(title = "Settings") },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Text("Settings", style = MaterialTheme.typography.headlineMedium, color = CareTailTextPrimary)
            Spacer(Modifier.height(4.dp))
            Text("CareTail is a local-first MVP for pet care tracking.", style = MaterialTheme.typography.bodyLarge, color = CareTailTextSecondary)
            Spacer(Modifier.height(24.dp))

            SettingsSection(title = "Premium") {
                SettingsRow(
                    title = "Manage Premium",
                    subtitle = if (isPremium) "Premium active" else "Free plan",
                    icon = Icons.Rounded.Star,
                    onClick = onOpenPremium,
                )
            }

            if (BuildConfig.DEBUG) {
                SettingsSection(title = "Developer / Testing") {
                    SettingsRow(
                        title = "Premium test mode",
                        subtitle = "Debug-only entitlement toggle for testing gates.",
                        icon = Icons.Rounded.Code,
                        trailing = {
                            Switch(
                                checked = isPremiumTestMode,
                                onCheckedChange = PremiumManager::setPremiumForTesting,
                            )
                        },
                    )
                }
            }

            SettingsSection(title = "Privacy") {
                SettingsRow(
                    title = "Privacy Policy",
                    subtitle = "Privacy Policy will be added before release.",
                    icon = Icons.Rounded.PrivacyTip,
                    onClick = { feedbackMessage = "Privacy Policy will be added before release." },
                )
                SettingsRow(
                    title = "Terms of Use",
                    subtitle = "Terms of Use will be added before release.",
                    icon = Icons.Rounded.Shield,
                    onClick = { feedbackMessage = "Terms of Use will be added before release." },
                )
                SettingsRow(
                    title = "No cloud sync",
                    subtitle = "MVP data stays on this device.",
                    icon = Icons.Rounded.CloudOff,
                )
                SettingsRow(
                    title = "Local-first data",
                    subtitle = "CareTail stores MVP pet care records locally on this device.",
                    icon = Icons.Rounded.PrivacyTip,
                )
            }

            SettingsSection(title = "Data") {
                SettingsRow(
                    title = "Export Data",
                    subtitle = "Full local data export is planned for a later version.",
                    icon = Icons.Rounded.UploadFile,
                    onClick = { feedbackMessage = "Full local data export is planned for a later version." },
                )
                SettingsRow(
                    title = "Documents",
                    subtitle = "View saved local pet records and files.",
                    icon = Icons.Rounded.Shield,
                    onClick = onOpenDocuments,
                )
                SettingsRow(
                    title = "Delete Local Data",
                    subtitle = "Remove CareTail records stored in the local database.",
                    icon = Icons.Rounded.DeleteOutline,
                    destructive = true,
                    onClick = { showDeleteConfirmation = true },
                )
            }

            SettingsSection(title = "About") {
                SettingsRow(
                    title = "CareTail",
                    subtitle = "Your Pet's Personal Care Tracker",
                    icon = Icons.Rounded.Info,
                )
                SettingsRow(
                    title = "App Version",
                    subtitle = "0.1.0",
                    icon = Icons.Rounded.Info,
                )
                SettingsRow(
                    title = "Local-first MVP",
                    subtitle = "Pet profiles, reminders, diary entries, and records are stored on this device.",
                    icon = Icons.Rounded.CloudOff,
                )
            }

            Spacer(Modifier.height(96.dp))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = CareTailTextPrimary,
        fontWeight = FontWeight.SemiBold,
    )
    Spacer(Modifier.height(10.dp))
    CareTailCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = CareTailWarmSurface,
        contentPadding = 12.dp,
    ) {
        content()
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    destructive: Boolean = false,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    val rowModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    Row(
        modifier = rowModifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (destructive) CareTailAccentSoft else CareTailPrimary.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (destructive) CareTailAccent else CareTailPrimaryDark,
                modifier = Modifier.size(23.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = if (destructive) CareTailAccent else CareTailTextPrimary,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = CareTailTextSecondary,
            )
        }
        when {
            trailing != null -> trailing()
            onClick != null -> Icon(
                imageVector = Icons.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = CareTailTextSecondary,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}
