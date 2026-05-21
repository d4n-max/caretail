package com.caretail.app.ui.screens.documents

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.caretail.app.data.repository.PetDocumentRepository
import com.caretail.app.data.repository.PetRepository
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.CareTailScaffold
import com.caretail.app.ui.components.CareTailTopBar
import com.caretail.app.ui.components.CoralFab
import com.caretail.app.ui.components.PrimaryCoralButton
import com.caretail.app.ui.components.ReminderTypeChip
import com.caretail.app.ui.components.SectionHeader
import com.caretail.app.ui.components.StatusPill
import com.caretail.app.ui.model.PetDocumentUiModel
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.theme.CareTailAccent
import com.caretail.app.ui.theme.CareTailAccentSoft
import com.caretail.app.ui.theme.CareTailTextPrimary
import com.caretail.app.ui.theme.CareTailTextSecondary
import com.caretail.app.ui.theme.CareTailWarmSurface
import com.caretail.app.ui.viewmodel.DocumentsViewModel
import com.caretail.app.ui.viewmodel.DocumentsViewModelFactory
import com.caretail.app.util.getMimeTypeFromUri

@Composable
fun DocumentsScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    petRepository: PetRepository,
    petDocumentRepository: PetDocumentRepository,
    onAddDocument: () -> Unit,
    onAddPet: () -> Unit,
) {
    val context = LocalContext.current
    val factory = remember(petRepository, petDocumentRepository) {
        DocumentsViewModelFactory(petRepository, petDocumentRepository)
    }
    val viewModel: DocumentsViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    fun openDocument(document: PetDocumentUiModel) {
        val uriText = document.fileUri
        if (uriText.isNullOrBlank()) {
            Toast.makeText(context, "No file attached.", Toast.LENGTH_SHORT).show()
            return
        }
        val uri = Uri.parse(uriText)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, getMimeTypeFromUri(context, uri))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, "No app available to open this file.", Toast.LENGTH_SHORT).show()
        } catch (_: SecurityException) {
            Toast.makeText(context, "Unable to open this file.", Toast.LENGTH_SHORT).show()
        }
    }

    CareTailScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        selectedBottomRoute = CareTailRoute.Settings.route,
        topBar = { CareTailTopBar(title = "Documents") },
        floatingActionButton = { CoralFab(onClick = onAddDocument) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text("Documents & Records", style = MaterialTheme.typography.headlineMedium, color = CareTailTextPrimary)
            Text(
                "Keep vet records, prescriptions, and care documents in one place.",
                style = MaterialTheme.typography.bodyLarge,
                color = CareTailTextSecondary,
            )
            Spacer(Modifier.height(16.dp))
            when {
                !uiState.isLoading && uiState.pets.isEmpty() -> EmptyPetsState(onAddPet)
                !uiState.isLoading -> {
                    if (!uiState.isPremium) {
                        CareTailCard(backgroundColor = CareTailWarmSurface) {
                            Text(
                                "Free documents: ${uiState.totalDocumentCount}/${uiState.freeDocumentLimit}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = CareTailTextSecondary,
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                    }
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        uiState.pets.forEach { pet ->
                            ReminderTypeChip(
                                text = pet.name,
                                selected = uiState.selectedPetId == pet.id,
                                onClick = { viewModel.onPetSelected(pet.id) },
                            )
                        }
                    }
                    Spacer(Modifier.height(22.dp))
                    if (!uiState.hasDocuments) {
                        EmptyDocumentsState(onAddDocument)
                    } else {
                        SectionHeader(uiState.pets.firstOrNull { it.id == uiState.selectedPetId }?.name ?: "Records")
                        Spacer(Modifier.height(12.dp))
                        uiState.documents.forEach { document ->
                            DocumentCard(
                                document = document,
                                onOpen = { openDocument(document) },
                                onDelete = { viewModel.deleteDocument(document) },
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.height(90.dp))
        }
    }
}

@Composable
private fun EmptyPetsState(onAddPet: () -> Unit) {
    CareTailCard(backgroundColor = CareTailWarmSurface) {
        Text("Add a pet first", style = MaterialTheme.typography.titleLarge, color = CareTailTextPrimary)
        Spacer(Modifier.height(8.dp))
        Text("Create a pet profile before adding records.", style = MaterialTheme.typography.bodyLarge, color = CareTailTextSecondary)
        Spacer(Modifier.height(16.dp))
        PrimaryCoralButton(text = "Add Pet", onClick = onAddPet)
    }
}

@Composable
private fun EmptyDocumentsState(onAddDocument: () -> Unit) {
    CareTailCard(backgroundColor = CareTailWarmSurface) {
        Text("No documents yet", style = MaterialTheme.typography.titleLarge, color = CareTailTextPrimary)
        Spacer(Modifier.height(8.dp))
        Text(
            "Add vaccine records, prescriptions, insurance files, or vet documents.",
            style = MaterialTheme.typography.bodyLarge,
            color = CareTailTextSecondary,
        )
        Spacer(Modifier.height(16.dp))
        PrimaryCoralButton(text = "Add Document", onClick = onAddDocument)
    }
}

@Composable
private fun DocumentCard(
    document: PetDocumentUiModel,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
) {
    CareTailCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                Icon(Icons.Rounded.Description, contentDescription = null, tint = CareTailAccent)
                Column {
                    Text(document.title, style = MaterialTheme.typography.titleMedium, color = CareTailTextPrimary)
                    Text(
                        "${document.petName} - Added ${document.createdDateLabel}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CareTailTextSecondary,
                    )
                }
            }
            Row {
                IconButton(onClick = onOpen) {
                    Icon(Icons.Rounded.OpenInNew, contentDescription = "Open", tint = CareTailAccent)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = CareTailTextSecondary)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusPill(document.type, backgroundColor = CareTailAccentSoft, contentColor = CareTailAccent)
            if (document.fileUri != null) {
                StatusPill("File", icon = Icons.Rounded.AttachFile)
            }
        }
        document.notes?.let { notes ->
            Spacer(Modifier.height(10.dp))
            Text(notes, style = MaterialTheme.typography.bodyMedium, color = CareTailTextSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}
