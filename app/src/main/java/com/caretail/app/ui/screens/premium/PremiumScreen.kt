package com.caretail.app.ui.screens.premium

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.PremiumBenefitRow
import com.caretail.app.ui.components.PricingCard
import com.caretail.app.ui.components.PrimaryCoralButton
import com.caretail.app.ui.components.StarIcon
import com.caretail.app.ui.theme.CareTailBackground
import com.caretail.app.ui.theme.CareTailPrimary
import com.caretail.app.ui.theme.CareTailPrimaryDark
import com.caretail.app.ui.theme.CareTailTextPrimary
import com.caretail.app.ui.theme.CareTailTextSecondary

@Composable
fun PremiumScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CareTailBackground)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart)) {
                Icon(Icons.Rounded.Close, contentDescription = "Close", tint = CareTailTextPrimary)
            }
        }
        Spacer(Modifier.height(26.dp))
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(CareTailPrimary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(StarIcon, contentDescription = null, tint = CareTailPrimaryDark, modifier = Modifier.size(52.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "CareTail Premium",
            style = MaterialTheme.typography.displaySmall,
            color = CareTailPrimaryDark,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Unlock the full potential of your pet care routine.",
            style = MaterialTheme.typography.bodyLarge,
            color = CareTailTextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(28.dp))
        CareTailCard(modifier = Modifier.fillMaxWidth()) {
            PremiumBenefitRow("Unlimited pets")
            Spacer(Modifier.height(18.dp))
            PremiumBenefitRow("Advanced reminders")
            Spacer(Modifier.height(18.dp))
            PremiumBenefitRow("Unlimited health diary")
            Spacer(Modifier.height(18.dp))
            PremiumBenefitRow("Unlimited documents")
            Spacer(Modifier.height(18.dp))
            PremiumBenefitRow("Exportable reports")
        }
        Spacer(Modifier.height(22.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
            PricingCard("Monthly", "$4.99", selected = false, modifier = Modifier.weight(1f))
            PricingCard("Yearly", "$29.99", "Only $2.49/mo", selected = true, badge = "Best Value", modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(28.dp))
        PrimaryCoralButton(text = "Start Premium", onClick = {})
        Spacer(Modifier.height(18.dp))
        Text("Restore Purchase", style = MaterialTheme.typography.labelLarge, color = CareTailTextSecondary)
    }
}
