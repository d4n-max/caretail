package com.caretail.app.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillingRepository(context: Context) : PurchasesUpdatedListener {
    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _uiState = MutableStateFlow(BillingUiState())
    val uiState: StateFlow<BillingUiState> = _uiState.asStateFlow()

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    private var isConnecting = false

    private val billingClient: BillingClient = BillingClient.newBuilder(appContext)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build(),
        )
        .build()

    fun startConnection() {
        if (billingClient.isReady || isConnecting) return
        isConnecting = true
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                isConnecting = false
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProducts()
                    queryActivePurchases(showFeedback = false)
                } else {
                    setUnavailable()
                }
            }

            override fun onBillingServiceDisconnected() {
                isConnecting = false
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Premium products are unavailable right now. Please try again later.",
                )
                startConnection()
            }
        })
    }

    fun launchPurchase(activity: Activity, plan: PremiumPlan) {
        val product = _uiState.value.products.firstOrNull { it.plan == plan }
        if (product == null) {
            emitMessage("Premium products are unavailable right now. Please try again later.")
            startConnection()
            return
        }

        val productDetailsParamsBuilder = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(product.productDetails)
        product.offerToken?.let(productDetailsParamsBuilder::setOfferToken)

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParamsBuilder.build()))
            .build()
        val result = billingClient.launchBillingFlow(activity, flowParams)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            emitMessage(friendlyBillingMessage(result.responseCode))
        }
    }

    fun restorePurchases() {
        if (!billingClient.isReady) {
            startConnection()
            emitMessage("Connecting to Google Play. Please try restore again in a moment.")
            return
        }
        queryActivePurchases(showFeedback = true)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> handlePurchases(purchases.orEmpty(), showRestoreFeedback = false)
            BillingClient.BillingResponseCode.USER_CANCELED -> emitMessage("Purchase canceled.")
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> queryActivePurchases(showFeedback = true)
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                emitMessage("Google Play Billing disconnected. Please try again.")
                startConnection()
            }
            else -> emitMessage(friendlyBillingMessage(billingResult.responseCode))
        }
    }

    private fun queryProducts() {
        val products = PremiumPlan.entries.map { plan ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(plan.productId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(products)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val mappedProducts = productDetailsResult.productDetailsList.mapNotNull(::mapProductDetails)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    products = mappedProducts,
                    errorMessage = if (mappedProducts.isEmpty()) {
                        "Premium products are unavailable right now. Please try again later."
                    } else {
                        null
                    },
                    isPremium = PremiumManager.isPremium.value,
                )
            } else {
                setUnavailable()
            }
        }
    }

    private fun queryActivePurchases(showFeedback: Boolean) {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                handlePurchases(purchases, showRestoreFeedback = showFeedback)
            } else if (showFeedback) {
                emitMessage(friendlyBillingMessage(billingResult.responseCode))
            }
        }
    }

    private fun handlePurchases(purchases: List<Purchase>, showRestoreFeedback: Boolean) {
        val activePurchases = purchases.filter { purchase ->
            purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                purchase.products.any { productId -> PremiumPlan.entries.any { it.productId == productId } }
        }

        activePurchases.forEach(::acknowledgeIfNeeded)

        val hasPremium = activePurchases.isNotEmpty()
        PremiumManager.setBillingEntitlement(hasPremium)
        _uiState.value = _uiState.value.copy(isPremium = PremiumManager.isPremium.value)

        if (showRestoreFeedback) {
            emitMessage(if (hasPremium) "Premium restored." else "No active Premium subscription found.")
        } else if (hasPremium) {
            emitMessage("CareTail Premium is active.")
        }
    }

    private fun acknowledgeIfNeeded(purchase: Purchase) {
        if (purchase.isAcknowledged) return
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { result ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                emitMessage("Purchase received, but acknowledgement is still pending.")
            }
        }
    }

    private fun mapProductDetails(productDetails: ProductDetails): PremiumProduct? {
        val plan = PremiumPlan.entries.firstOrNull { it.productId == productDetails.productId } ?: return null
        val offer = productDetails.subscriptionOfferDetails?.firstOrNull()
        val pricingPhase = offer?.pricingPhases?.pricingPhaseList?.firstOrNull()
        return PremiumProduct(
            plan = plan,
            productId = productDetails.productId,
            title = productDetails.title,
            description = productDetails.description,
            price = pricingPhase?.formattedPrice ?: plan.fallbackPrice,
            productDetails = productDetails,
            offerToken = offer?.offerToken,
        )
    }

    private fun setUnavailable() {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = "Premium products are unavailable right now. Please try again later.",
        )
    }

    private fun emitMessage(message: String) {
        scope.launch { _messages.emit(message) }
    }

    private fun friendlyBillingMessage(responseCode: Int): String = when (responseCode) {
        BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> "Google Play Billing is unavailable. Please try again later."
        BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> "Google Play Billing is not available on this device."
        BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> "This Premium plan is unavailable right now."
        BillingClient.BillingResponseCode.ERROR -> "Google Play Billing could not complete that request."
        else -> "Premium could not be started. Please try again later."
    }

    // TODO: Add server-side purchase verification before relying on client-only entitlement for production launch.
}
