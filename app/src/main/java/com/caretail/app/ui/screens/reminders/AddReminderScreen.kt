package com.caretail.app.ui.screens.reminders

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.caretail.app.billing.PremiumUpsellReason
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.data.repository.ReminderRepository
import com.caretail.app.reminders.NotificationPreferences
import com.caretail.app.reminders.ReminderNotificationScheduler
import com.caretail.app.review.ReviewPromptManager
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.CareTailScaffold
import com.caretail.app.ui.components.CareTailTopBar
import com.caretail.app.ui.components.PrimaryCoralButton
import com.caretail.app.ui.components.ReminderTypeChip
import com.caretail.app.ui.components.SectionHeader
import com.caretail.app.ui.components.TextActionButton
import com.caretail.app.ui.components.careTailOutlinedTextFieldColors
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.theme.CareTailAccent
import com.caretail.app.ui.theme.CareTailCard
import com.caretail.app.ui.theme.CareTailPrimary
import com.caretail.app.ui.theme.CareTailPrimaryDark
import com.caretail.app.ui.theme.CareTailTextPrimary
import com.caretail.app.ui.theme.CareTailTextSecondary
import com.caretail.app.ui.theme.CareTailWarmSurface
import com.caretail.app.ui.viewmodel.AddReminderViewModel
import com.caretail.app.ui.viewmodel.AddReminderViewModelFactory
import com.caretail.app.ui.viewmodel.PremiumRepeatTypes
import com.caretail.app.ui.viewmodel.ReminderTypes
import com.caretail.app.ui.viewmodel.RepeatTypes
import com.caretail.app.util.formatDate
import java.util.Calendar

@Composable
fun AddReminderScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    petRepository: PetRepository,
    reminderRepository: ReminderRepository,
    reminderNotificationScheduler: ReminderNotificationScheduler,
    notificationPreferences: NotificationPreferences,
    reviewPromptManager: ReviewPromptManager,
    preselectedPetId: Long?,
    editReminderId: Long? = null,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onAddPet: () -> Unit,
    onOpenPremium: (PremiumUpsellReason) -> Unit,
) {
    val factory = remember(petRepository, reminderRepository, reviewPromptManager, preselectedPetId, editReminderId) {
        AddReminderViewModelFactory(petRepository, reminderRepository, reminderNotificationScheduler, reviewPromptManager, preselectedPetId, editReminderId)
    }
    val viewModel: AddReminderViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val textFieldColors = careTailOutlinedTextFieldColors()
    val validationMessage = uiState.validationError.orEmpty()
    var showNotificationExplanation by remember { mutableStateOf(false) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        notificationPreferences.setCareRemindersEnabled(granted)
        viewModel.saveReminder(notificationPermissionGranted = granted)
    }

    fun hasNotificationPermission(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    fun saveWithNotificationPermissionCheck() {
        if (!viewModel.validateBeforePermissionRequest()) return
        if (!notificationPreferences.areCareRemindersEnabled()) {
            viewModel.saveReminder(notificationPermissionGranted = false)
        } else if (
            uiState.editingReminderId == null &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !hasNotificationPermission()
        ) {
            showNotificationExplanation = true
        } else {
            viewModel.saveReminder(notificationPermissionGranted = hasNotificationPermission())
        }
    }

    fun showDatePicker() {
        val selectedDate = Calendar.getInstance().apply {
            timeInMillis = uiState.selectedDateMillis
        }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth -> viewModel.onDateSelected(year, month, dayOfMonth) },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH),
        ).show()
    }

    fun showTimePicker() {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute -> viewModel.onTimeSelected(hourOfDay, minute) },
            uiState.selectedHour,
            uiState.selectedMinute,
            true,
        ).show()
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            uiState.successMessage?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
            onSaved()
        }
    }
    LaunchedEffect(uiState.upsellReason) {
        uiState.upsellReason?.let { reason ->
            onOpenPremium(reason)
            viewModel.onPremiumNavigationConsumed()
        }
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
                        viewModel.saveReminder(notificationPermissionGranted = false)
                    },
                )
            },
        )
    }

    CareTailScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        selectedBottomRoute = CareTailRoute.Reminders.route,
        topBar = { CareTailTopBar(title = if (uiState.editingReminderId == null) "Add Reminder" else "Edit Reminder", showBack = true, onBack = onBack) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            if (!uiState.isLoading && uiState.pets.isEmpty()) {
                EmptyPetsState(onAddPet = onAddPet)
            } else {
                SectionHeader("Who is this for?")
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    uiState.pets.forEach { pet ->
                        ReminderTypeChip(
                            text = pet.name,
                            selected = uiState.selectedPetId == pet.id,
                            onClick = { viewModel.onPetSelected(pet.id) },
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
                CareTailCard {
                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = viewModel::onTitleChanged,
                        label = { Text("Title *") },
                        placeholder = { Text("Rabies booster, heartworm meds...") },
                        singleLine = true,
                        isError = validationMessage.contains("title", ignoreCase = true),
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Reminder type *", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(10.dp))
                    ReminderChipRows(
                        values = ReminderTypes,
                        selectedValue = uiState.type,
                        onSelected = viewModel::onTypeSelected,
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DateTimeSelectorCard(
                            label = "Date *",
                            value = formatDate(uiState.selectedDateMillis),
                            icon = Icons.Rounded.Event,
                            contentDescription = "Select reminder date",
                            isError = validationMessage.contains("date", ignoreCase = true),
                            modifier = Modifier.weight(1f),
                            onClick = ::showDatePicker,
                        )
                        DateTimeSelectorCard(
                            label = "Time *",
                            value = uiState.time,
                            icon = Icons.Rounded.Schedule,
                            contentDescription = "Select reminder time",
                            isError = validationMessage.contains("time", ignoreCase = true),
                            modifier = Modifier.weight(1f),
                            onClick = ::showTimePicker,
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Repeat", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(10.dp))
                    ReminderChipRows(
                        values = RepeatTypes,
                        selectedValue = uiState.repeatType,
                        premiumValues = PremiumRepeatTypes,
                        onSelected = viewModel::onRepeatTypeSelected,
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = viewModel::onNotesChanged,
                        label = { Text("Notes") },
                        placeholder = { Text("Add any details here...", color = CareTailTextSecondary) },
                        leadingIcon = { androidx.compose.material3.Icon(Icons.Rounded.Notes, contentDescription = null) },
                        colors = textFieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                    )
                }
                uiState.validationError?.let { message ->
                    Spacer(Modifier.height(12.dp))
                    Text(message, color = CareTailAccent, style = MaterialTheme.typography.bodyMedium)
                }
                uiState.generalError?.let { message ->
                    Spacer(Modifier.height(12.dp))
                    Text(message, color = CareTailAccent, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(24.dp))
                PrimaryCoralButton(
                    text = if (uiState.isLoading) "Saving..." else if (uiState.editingReminderId == null) "Save Reminder" else "Save Changes",
                    onClick = ::saveWithNotificationPermissionCheck,
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DateTimeSelectorCard(
    label: String,
    value: String,
    icon: ImageVector,
    contentDescription: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .height(78.dp)
            .semantics { this.contentDescription = contentDescription }
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = CareTailCard,
        shadowElevation = 1.dp,
        border = BorderStroke(
            width = if (isError) 2.dp else 1.dp,
            color = if (isError) CareTailAccent else CareTailPrimary.copy(alpha = 0.55f),
        ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier.size(34.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = CareTailPrimaryDark, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isError) CareTailAccent else CareTailTextSecondary,
                    maxLines = 1,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = CareTailTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun EmptyPetsState(onAddPet: () -> Unit) {
    CareTailCard(backgroundColor = CareTailWarmSurface) {
        Text("Add a pet first", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text(
            "Create a pet profile before adding care reminders.",
            style = MaterialTheme.typography.bodyLarge,
            color = CareTailTextSecondary,
        )
        Spacer(Modifier.height(16.dp))
        PrimaryCoralButton(text = "Add Pet", onClick = onAddPet)
    }
}

@Composable
private fun ReminderChipRows(
    values: List<String>,
    selectedValue: String,
    premiumValues: List<String> = emptyList(),
    onSelected: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        values.chunked(3).forEach { rowValues ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowValues.forEach { value ->
                    ReminderTypeChip(
                        text = value,
                        selected = selectedValue == value,
                        trailingBadgeText = if (value in premiumValues) "Premium" else null,
                        onClick = { onSelected(value) },
                    )
                }
            }
        }
    }
}
