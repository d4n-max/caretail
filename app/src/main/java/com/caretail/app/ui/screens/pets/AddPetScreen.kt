package com.caretail.app.ui.screens.pets

import androidx.compose.runtime.Composable
import com.caretail.app.ui.components.CareTailScaffold
import com.caretail.app.ui.components.CareTailTopBar
import com.caretail.app.ui.components.PrimaryCoralButton
import com.caretail.app.ui.navigation.CareTailRoute
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddPetScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
) {
    CareTailScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        selectedBottomRoute = CareTailRoute.Pets.route,
        topBar = { CareTailTopBar(title = "Add Pet", showBack = true, onBack = onBack) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text("Pet details", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = "Luna", onValueChange = {}, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = "Cat", onValueChange = {}, label = { Text("Species") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = "British Shorthair", onValueChange = {}, label = { Text("Breed") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(24.dp))
            PrimaryCoralButton(text = "Save Pet", onClick = onBack)
        }
    }
}
