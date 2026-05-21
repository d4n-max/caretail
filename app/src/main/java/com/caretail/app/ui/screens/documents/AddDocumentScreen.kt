package com.caretail.app.ui.screens.documents

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material3.Icon
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.caretail.app.data.repository.PetDocumentRepository
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.CareTailScaffold
import com.caretail.app.ui.components.CareTailTopBar
import com.caretail.app.ui.components.PrimaryCoralButton
import com.caretail.app.ui.components.ReminderTypeChip
import com.caretail.app.ui.components.SecondaryButton
import com.caretail.app.ui.components.SectionHeader
import com.caretail.app.ui.components.careTailOutlinedTextFieldColors
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.theme.CareTailAccent
import com.caretail.app.ui.theme.CareTailTextSecondary
import com.caretail.app.ui.theme.CareTailWarmSurface
import com.caretail.app.ui.viewmodel.AddDocumentViewModel
import com.caretail.app.ui.viewmodel.AddDocumentViewModelFactory
import com.caretail.app.ui.viewmodel.DocumentTypeValues
import com.caretail.app.util.getDisplayNameFromUri

private val DocumentMimeTypes = arrayOf(
    "application/pdf",
    "image/*",
    "text/*",
    "application/msword",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
)

@Composable
fun AddDocumentScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    petRepository: PetRepository,
    petDocumentRepository: PetDocumentRepository,
    preselectedPetId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onAddPet: () -> Unit,
) {
    val context = LocalContext.current
    val factory = remember(petRepository, petDocumentRepository, preselectedPetId) {
        AddDocumentViewModelFactory(petRepository, petDocumentRepository, preselectedPetId)
    }
    val viewModel: AddDocumentViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val textFieldColors = careTailOutlinedTextFieldColors()
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            runCatching {
                context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            viewModel.onFileSelected(it.toString(), getDisplayNameFromUri(context, it))
        }
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) onSaved()
    }

    CareTailScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        selectedBottomRoute = CareTailRoute.Settings.route,
        topBar = { CareTailTopBar(title = "Add Document", showBack = true, onBack = onBack) },
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
                CareTailCard {
                    SectionHeader("Pet")
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
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = viewModel::onTitleChanged,
                        label = { Text("Title *") },
                        singleLine = true,
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Document type", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(10.dp))
                    ChipRows(DocumentTypeValues, uiState.type, viewModel::onTypeSelected)
                    Spacer(Modifier.height(16.dp))
                    SecondaryButton(text = "Choose file", onClick = { picker.launch(DocumentMimeTypes) })
                    uiState.fileName?.let { name ->
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Rounded.AttachFile, contentDescription = null, tint = CareTailAccent)
                            Text(name, style = MaterialTheme.typography.bodyMedium, color = CareTailTextSecondary)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = viewModel::onNotesChanged,
                        label = { Text("Notes") },
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
                    text = if (uiState.isLoading) "Saving..." else "Save Document",
                    onClick = viewModel::saveDocument,
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
        Text("Create a pet profile before adding records.", style = MaterialTheme.typography.bodyLarge, color = CareTailTextSecondary)
        Spacer(Modifier.height(16.dp))
        PrimaryCoralButton(text = "Add Pet", onClick = onAddPet)
    }
}

@Composable
private fun ChipRows(
    values: List<String>,
    selectedValue: String,
    onSelected: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        values.chunked(2).forEach { rowValues ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowValues.forEach { value ->
                    ReminderTypeChip(text = value, selected = selectedValue == value, onClick = { onSelected(value) })
                }
            }
        }
    }
}
