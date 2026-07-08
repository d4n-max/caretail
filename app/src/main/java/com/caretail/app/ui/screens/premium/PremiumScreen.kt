package com.caretail.app.ui.screens.premium

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.caretail.app.analytics.AnalyticsTracker
import com.caretail.app.BuildConfig
import com.caretail.app.R
import com.caretail.app.billing.BillingStatus
import com.caretail.app.billing.BillingRepository
import com.caretail.app.billing.PremiumPlan
import com.caretail.app.billing.PremiumUpsellReason
import com.caretail.app.billing.analyticsValue
import com.caretail.app.ui.components.CareTailCard
import com.caretail.app.ui.components.PremiumBenefitRow
import com.caretail.app.ui.components.PricingCard
import com.caretail.app.ui.components.PrimaryCoralButton
import com.caretail.app.ui.components.TextActionButton
import com.caretail.app.ui.theme.CareTailBackground
import com.caretail.app.ui.theme.CareTailPrimary
import com.caretail.app.ui.theme.CareTailPrimaryDark
import com.caretail.app.ui.theme.CareTailTextPrimary
import com.caretail.app.ui.theme.CareTailTextSecondary
import com.caretail.app.ui.theme.CareTailWarmSurface
import com.caretail.app.util.findActivity

@Composable
fun PremiumScreen(
    reason: PremiumUpsellReason?,
    billingRepository: BillingRepository,
    analyticsTracker: AnalyticsTracker,
    onClose: () -> Unit,
    onMaybeLater: () -> Unit = onClose,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val billingState by billingRepository.uiState.collectAsState()
    var selectedPlan by remember { mutableStateOf(PremiumPlan.Yearly) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    val monthlyProduct = billingState.products.firstOrNull { it.plan == PremiumPlan.Monthly }
    val yearlyProduct = billingState.products.firstOrNull { it.plan == PremiumPlan.Yearly }
    val productsUnavailable = billingState.status == BillingStatus.ProductsUnavailable
    val purchaseInProgress = billingState.status == BillingStatus.PurchaseInProgress
    val selectedProduct = billingState.products.firstOrNull { it.plan == selectedPlan }

    BackHandler(onBack = onClose)

    LaunchedEffect(billingRepository) {
        billingRepository.startConnection()
        billingRepository.messages.collect { message -> feedbackMessage = message }
    }

    LaunchedEffect(reason) {
        analyticsTracker.trackPaywallViewed(
            sourceScreen = AnalyticsTracker.Screens.Premium,
            paywallReason = reason?.routeValue ?: AnalyticsTracker.SourceNavigation,
            plan = selectedPlan.analyticsValue,
        )
    }

    feedbackMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { feedbackMessage = null },
            confirmButton = {
                TextActionButton(text = "OK", onClick = { feedbackMessage = null })
            },
            title = { Text("CareTail Premium") },
            text = { Text(message) },
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CareTailBackground),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 72.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(98.dp)
                    .clip(CircleShape)
                    .background(CareTailPrimary.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.caretail_mark_colored),
                    contentDescription = "CareTail Premium",
                    modifier = Modifier.size(78.dp),
                )
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
                "Keep every pet's care organized, from reminders to health history and vet-ready reports.",
                style = MaterialTheme.typography.bodyLarge,
                color = CareTailTextSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(28.dp))
            reason?.let { upsellReason ->
                CareTailCard(modifier = Modifier.fillMaxWidth(), backgroundColor = CareTailWarmSurface) {
                    Text(
                        upsellReason.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = CareTailTextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        upsellReason.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CareTailTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(Modifier.height(18.dp))
            }
            CareTailCard(modifier = Modifier.fillMaxWidth()) {
                val benefits = listOf(
                    "Unlimited pets",
                    "Unlimited reminders",
                    "Advanced repeat schedules",
                    "Export care reports",
                    "Organize documents and care history",
                    "No ads. No selling your data.",
                )
                benefits.forEachIndexed { index, benefit ->
                    if (index > 0) Spacer(Modifier.height(18.dp))
                    PremiumBenefitRow(benefit)
                }
            }
            Spacer(Modifier.height(16.dp))
            CareTailCard(modifier = Modifier.fillMaxWidth(), backgroundColor = CareTailWarmSurface) {
                Text(
                    "Your free pet profile stays free. You can cancel anytime in Google Play.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CareTailTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.height(22.dp))
            CareTailCard(modifier = Modifier.fillMaxWidth()) {
                Text("Free includes", style = MaterialTheme.typography.titleMedium, color = CareTailTextPrimary)
                Spacer(Modifier.height(10.dp))
                PremiumBenefitRow("1 pet profile")
                Spacer(Modifier.height(18.dp))
                PremiumBenefitRow("5 active reminders")
                Spacer(Modifier.height(18.dp))
                PremiumBenefitRow("5 health diary entries")
                Spacer(Modifier.height(18.dp))
                PremiumBenefitRow("3 document records")
                Spacer(Modifier.height(18.dp))
                PremiumBenefitRow("Basic local reminder notifications")
                Spacer(Modifier.height(18.dp))
                PremiumBenefitRow("Edit and delete existing data")
            }
            Spacer(Modifier.height(22.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                PricingCard(
                    title = "Monthly",
                    price = monthlyProduct?.price ?: if (productsUnavailable) "Unavailable" else "Loading...",
                    selected = selectedPlan == PremiumPlan.Monthly,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedPlan = PremiumPlan.Monthly },
                )
                PricingCard(
                    title = "Yearly",
                    price = yearlyProduct?.price ?: if (productsUnavailable) "Unavailable" else "Loading...",
                    detail = "Less than $1.70/month",
                    badge = "Best value",
                    selected = selectedPlan == PremiumPlan.Yearly,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedPlan = PremiumPlan.Yearly },
                )
            }
            billingState.errorMessage?.let { message ->
                Spacer(Modifier.height(12.dp))
                Text(
                    message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CareTailTextSecondary,
                    textAlign = TextAlign.Center,
                )
            }
            if (billingState.isPremium) {
                Spacer(Modifier.height(12.dp))
                Text(
                    "Premium active",
                    style = MaterialTheme.typography.titleMedium,
                    color = CareTailPrimaryDark,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(28.dp))
            PrimaryCoralButton(
                text = when {
                    billingState.isLoading -> "Loading Premium..."
                    purchaseInProgress -> "Starting..."
                    billingState.isPremium -> "Premium Active"
                    else -> "Start Premium"
                },
                enabled = !billingState.isLoading && !purchaseInProgress && !billingState.isPremium && selectedProduct != null,
                onClick = {
                    analyticsTracker.trackUpgradeClicked(
                        sourceScreen = AnalyticsTracker.Screens.Premium,
                        paywallReason = reason?.routeValue ?: AnalyticsTracker.SourcePaywall,
                        plan = selectedPlan.analyticsValue,
                    )
                    analyticsTracker.trackPurchaseStarted(
                        plan = selectedPlan.analyticsValue,
                        source = AnalyticsTracker.SourcePaywall,
                    )
                    val activity = context.findActivity()
                    if (activity == null) {
                        analyticsTracker.trackPurchaseFailed(
                            plan = selectedPlan.analyticsValue,
                            source = AnalyticsTracker.SourcePaywall,
                            result = AnalyticsTracker.Results.Failed,
                            errorType = "activity_unavailable",
                        )
                        feedbackMessage = "Premium could not be started from this screen. Please try again."
                    } else {
                        billingRepository.launchPurchase(activity, selectedPlan)
                    }
                },
            )
            if (BuildConfig.DEBUG) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Debug builds can use Premium test mode from Settings.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CareTailTextSecondary,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextActionButton(text = "Continue with Free", onClick = onMaybeLater)
                TextActionButton(
                    text = "Restore purchase",
                    onClick = {
                        analyticsTracker.trackRestorePurchasesClicked(AnalyticsTracker.Screens.Premium)
                        billingRepository.restorePurchases()
                    },
                )
            }
        }

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 12.dp, top = 8.dp)
                .size(48.dp)
                .zIndex(1f),
        ) {
            Icon(
                Icons.Rounded.Close,
                contentDescription = "Close Premium",
                tint = CareTailTextPrimary,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}
