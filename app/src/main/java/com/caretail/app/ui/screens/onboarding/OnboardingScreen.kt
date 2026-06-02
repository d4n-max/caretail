package com.caretail.app.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caretail.app.R
import com.caretail.app.auth.AuthUiState
import com.caretail.app.ui.components.PrimaryCoralButton
import com.caretail.app.ui.components.SecondaryCareTailButton
import com.caretail.app.ui.components.TextActionButton
import com.caretail.app.ui.theme.CareTailBackground
import com.caretail.app.ui.theme.CareTailPrimary
import com.caretail.app.ui.theme.CareTailPrimaryDark
import com.caretail.app.ui.theme.CareTailTextPrimary
import com.caretail.app.ui.theme.CareTailTextSecondary
import com.caretail.app.ui.theme.CareTailWarmSurface
import com.caretail.app.util.findActivity

@Composable
fun OnboardingScreen(
    authUiState: AuthUiState,
    onGoogleSignIn: (android.app.Activity?) -> Unit,
    onClearAuthError: () -> Unit,
    onGetStarted: () -> Unit,
) {
    val context = LocalContext.current

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CareTailBackground)
            .padding(horizontal = 28.dp, vertical = 38.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        OnboardingCareTailMark(
            modifier = Modifier
                .fillMaxWidth()
                .height(196.dp),
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "CareTail",
            style = MaterialTheme.typography.titleMedium,
            color = CareTailPrimaryDark,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Care for every tail, all in one place.",
            modifier = Modifier
                .widthIn(max = 340.dp)
                .padding(horizontal = 4.dp),
            style = MaterialTheme.typography.headlineMedium,
            color = CareTailTextPrimary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Track vaccines, medication, vet visits, health notes, and documents for your pets.",
            modifier = Modifier
                .widthIn(max = 340.dp)
                .padding(horizontal = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = CareTailTextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 21.sp,
        )
        Spacer(Modifier.height(28.dp))
        PrimaryCoralButton(text = "Get started", onClick = onGetStarted)
        Spacer(Modifier.height(12.dp))
        SecondaryCareTailButton(
            text = if (authUiState.isLoading) "Signing in..." else "Continue with Google",
            enabled = !authUiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
            onClick = { onGoogleSignIn(context.findActivity()) },
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = buildAnnotatedString {
                append("Login is optional. ")
                withStyle(SpanStyle(color = CareTailPrimaryDark, fontWeight = FontWeight.Bold)) {
                    append("You can continue locally.")
                }
            },
            style = MaterialTheme.typography.bodyLarge,
            color = CareTailTextSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun OnboardingCareTailMark(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    listOf(CareTailWarmSurface, CareTailPrimary.copy(alpha = 0.14f)),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.caretail_mark_colored),
            contentDescription = "CareTail mark",
            modifier = Modifier.size(92.dp),
        )
    }
}
