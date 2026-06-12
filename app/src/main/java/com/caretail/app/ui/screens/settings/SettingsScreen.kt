package com.caretail.app.ui.screens.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.caretail.app.auth.AuthUiState
import com.caretail.app.BuildConfig
import com.caretail.app.billing.BillingRepository
import com.caretail.app.billing.PremiumManager
import com.caretail.app.data.repository.HealthDiaryRepository
import com.caretail.app.data.repository.PetDocumentRepository
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.data.repository.ReminderRepository
import com.caretail.app.reminders.NotificationPreferences
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
import com.caretail.app.util.findActivity
import androidx.core.content.ContextCompat
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
    notificationPreferences: NotificationPreferences,
    billingRepository: BillingRepository,
    authUiState: AuthUiState,
    onGoogleSignIn: (android.app.Activity?) -> Unit,
    onSignOut: () -> Unit,
    onClearAuthError: () -> Unit,
    onLocalDataDeleted: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isPremium by PremiumManager.isPremium.collectAsState()
    val isPremiumTestMode by PremiumManager.isPremiumTestMode.collectAsState()
    val careRemindersEnabled by notificationPreferences.careRemindersEnabled.collectAsState()
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showNotificationExplanation by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        notificationPreferences.setCareRemindersEnabled(granted)
        if (granted) {
            scope.launch {
                rescheduleFutureReminders(
                    reminderRepository = reminderRepository,
                    petRepository = petRepository,
                    reminderNotificationScheduler = reminderNotificationScheduler,
                )
            }
        } else {
            feedbackMessage = "Reminders were saved, but notifications are off."
        }
    }

    fun hasNotificationPermission(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    fun disableCareReminderNotifications() {
        notificationPreferences.setCareRemindersEnabled(false)
        scope.launch {
            reminderRepository.getAllReminders().forEach { reminder ->
                reminderNotificationScheduler.cancelReminder(reminder.id)
            }
        }
    }

    fun enableCareReminderNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission()) {
            showNotificationExplanation = true
            return
        }
        notificationPreferences.setCareRemindersEnabled(true)
        scope.launch {
            rescheduleFutureReminders(
                reminderRepository = reminderRepository,
                petRepository = petRepository,
                reminderNotificationScheduler = reminderNotificationScheduler,
            )
        }
    }

    authUiState.errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = onClearAuthError,
            confirmButton = {
                TextActionButton(text = "OK", onClick = onClearAuthError)
            },
            title = { Text("Google Sign-In") },
            text = { Text(message) },
        )
    }

    androidx.compose.runtime.LaunchedEffect(billingRepository) {
        billingRepository.messages.collect { message -> feedbackMessage = message }
    }

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

    if (showNotificationExplanation) {
        AlertDialog(
            onDismissRequest = { showNotificationExplanation = false },
            title = { Text("Enable care reminders?") },
            text = {
                Text("CareTail can remind you about pet care tasks like vaccines, medication, grooming, and vet visits.")
            },
            confirmButton = {
                TextActionButton(
                    text = "Enable reminders",
                    onClick = {
                        showNotificationExplanation = false
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    },
                )
            },
            dismissButton = {
                TextActionButton(
                    text = "Not now",
                    onClick = {
                        showNotificationExplanation = false
                        notificationPreferences.setCareRemindersEnabled(false)
                    },
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
                SettingsRow(
                    title = "Restore purchases",
                    subtitle = "Check Google Play for an active CareTail Premium subscription.",
                    icon = Icons.Rounded.Star,
                    onClick = billingRepository::restorePurchases,
                )
            }

            SettingsSection(title = "Notifications") {
                val notificationsAvailable = hasNotificationPermission()
                SettingsRow(
                    title = "Care reminders",
                    subtitle = when {
                        careRemindersEnabled && notificationsAvailable -> "Local alerts for reminders you create."
                        careRemindersEnabled -> "Needs notification permission."
                        else -> "Reminder notifications are off."
                    },
                    icon = Icons.Rounded.Info,
                    trailing = {
                        Switch(
                            checked = careRemindersEnabled && notificationsAvailable,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    enableCareReminderNotifications()
                                } else {
                                    disableCareReminderNotifications()
                                }
                            },
                        )
                    },
                )
                SettingsRow(
                    title = "Gentle overdue reminder",
                    subtitle = "Planned. CareTail will not send overdue follow-ups yet.",
                    icon = Icons.Rounded.Info,
                )
                SettingsRow(
                    title = "Quiet hours",
                    subtitle = "Planned fixed window: 21:00 to 08:00.",
                    icon = Icons.Rounded.Info,
                )
            }

            SettingsSection(title = "Account") {
                val user = authUiState.user
                if (user == null) {
                    SettingsRow(
                        title = if (authUiState.isLoading) "Signing in..." else "Sign in with Google",
                        subtitle = "Optional. Cloud sync is not enabled yet.",
                        icon = Icons.Rounded.Info,
                        onClick = {
                            if (!authUiState.isLoading) {
                                onGoogleSignIn(context.findActivity())
                            }
                        },
                    )
                } else {
                    SettingsRow(
                        title = user.displayName ?: "Signed in",
                        subtitle = user.email ?: "Your data remains stored on this device in the MVP.",
                        icon = Icons.Rounded.Info,
                    )
                    SettingsRow(
                        title = "Sign out",
                        subtitle = "Your data remains stored on this device in the MVP.",
                        icon = Icons.Rounded.CloudOff,
                        onClick = onSignOut,
                    )
                }
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

private suspend fun rescheduleFutureReminders(
    reminderRepository: ReminderRepository,
    petRepository: PetRepository,
    reminderNotificationScheduler: ReminderNotificationScheduler,
) {
    val now = System.currentTimeMillis()
    reminderRepository.getAllReminders()
        .filter { reminder -> !reminder.isCompleted && reminder.dueAtMillis > now }
        .forEach { reminder ->
            val petName = petRepository.getPetById(reminder.petId)?.name.orEmpty()
            reminderNotificationScheduler.rescheduleReminder(reminder, petName)
        }
}
