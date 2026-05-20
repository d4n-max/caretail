package com.caretail.app.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.caretail.app.ui.components.PetImagePlaceholder
import com.caretail.app.ui.components.PrimaryCoralButton
import com.caretail.app.ui.theme.CareTailBackground
import com.caretail.app.ui.theme.CareTailPrimaryDark
import com.caretail.app.ui.theme.CareTailTextPrimary
import com.caretail.app.ui.theme.CareTailTextSecondary

@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CareTailBackground)
            .padding(horizontal = 28.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        PetImagePlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
        )
        Spacer(Modifier.height(30.dp))
        Text(
            text = "CareTail",
            style = MaterialTheme.typography.titleLarge,
            color = CareTailPrimaryDark,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(18.dp))
        Text(
            text = "Care for every tail, all in one place.",
            style = MaterialTheme.typography.displaySmall,
            color = CareTailTextPrimary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = "Track vaccines, medication, vet visits, health notes, and documents for your pets.",
            style = MaterialTheme.typography.bodyLarge,
            color = CareTailTextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))
        PrimaryCoralButton(text = "Get started", onClick = onGetStarted)
        Spacer(Modifier.height(20.dp))
        Text(
            text = buildAnnotatedString {
                append("Already have an account? ")
                withStyle(SpanStyle(color = CareTailPrimaryDark, fontWeight = FontWeight.Bold)) {
                    append("Log in")
                }
            },
            style = MaterialTheme.typography.bodyLarge,
            color = CareTailTextSecondary,
            textAlign = TextAlign.Center,
        )
    }
}
