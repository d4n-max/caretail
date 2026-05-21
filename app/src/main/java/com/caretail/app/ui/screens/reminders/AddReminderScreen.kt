package com.caretail.app.ui.screens.reminders

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.data.repository.ReminderRepository
import com.caretail.app.reminders.ReminderNotificationScheduler
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.CareTailScaffold
import com.caretail.app.ui.components.CareTailTopBar
import com.caretail.app.ui.components.PrimaryCoralButton
import com.caretail.app.ui.components.ReminderTypeChip
import com.caretail.app.ui.components.SectionHeader
import com.caretail.app.ui.components.careTailOutlinedTextFieldColors
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.theme.CareTailAccent
import com.caretail.app.ui.theme.CareTailTextSecondary
import com.caretail.app.ui.theme.CareTailWarmSurface
import com.caretail.app.ui.viewmodel.AddReminderViewModel
import com.caretail.app.ui.viewmodel.AddReminderViewModelFactory
import com.caretail.app.ui.viewmodel.ReminderTypes
import com.caretail.app.ui.viewmodel.RepeatTypes

@Composable
fun AddReminderScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    petRepository: PetRepository,
    reminderRepository: ReminderRepository,
    reminderNotificationScheduler: ReminderNotificationScheduler,
    preselectedPetId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onAddPet: () -> Unit,
) {
    val factory = remember(petRepository, reminderRepository, preselectedPetId) {
        AddReminderViewModelFactory(petRepository, reminderRepository, reminderNotificationScheduler, preselectedPetId)
    }
    val viewModel: AddReminderViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val textFieldColors = careTailOutlinedTextFieldColors()
    val validationMessage = uiState.validationError.orEmpty()
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        viewModel.saveReminder(notificationPermissionGranted = granted)
    }

    fun hasNotificationPermission(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    fun saveWithNotificationPermissionCheck() {
        if (!viewModel.validateBeforePermissionRequest()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission()) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.saveReminder(notificationPermissionGranted = true)
        }
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            uiState.successMessage?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
            onSaved()
        }
    }

    CareTailScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        selectedBottomRoute = CareTailRoute.Reminders.route,
        topBar = { CareTailTopBar(title = "Add Reminder", showBack = true, onBack = onBack) },
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
                        OutlinedTextField(
                            value = uiState.date,
                            onValueChange = viewModel::onDateChanged,
                            label = { Text("Date *") },
                            placeholder = { Text("YYYY-MM-DD") },
                            singleLine = true,
                            isError = validationMessage.contains("date", ignoreCase = true),
                            colors = textFieldColors,
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedTextField(
                            value = uiState.time,
                            onValueChange = viewModel::onTimeChanged,
                            label = { Text("Time *") },
                            placeholder = { Text("HH:mm") },
                            singleLine = true,
                            isError = validationMessage.contains("time", ignoreCase = true),
                            colors = textFieldColors,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Repeat", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(10.dp))
                    ReminderChipRows(
                        values = RepeatTypes,
                        selectedValue = uiState.repeatType,
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
                    text = if (uiState.isLoading) "Saving..." else "Save Reminder",
                    onClick = ::saveWithNotificationPermissionCheck,
                )
            }
            Spacer(Modifier.height(24.dp))
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
    onSelected: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        values.chunked(3).forEach { rowValues ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowValues.forEach { value ->
                    ReminderTypeChip(
                        text = value,
                        selected = selectedValue == value,
                        onClick = { onSelected(value) },
                    )
                }
            }
        }
    }
}
