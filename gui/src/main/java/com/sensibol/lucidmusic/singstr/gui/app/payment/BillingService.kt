package com.sensibol.lucidmusic.singstr.gui.app.payment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.android.billingclient.api.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class BillingService(
    private val context: Context,
    private val subscriptionSkuKeys: List<String>
) : IBillingService(), PurchasesUpdatedListener, BillingClientStateListener, AcknowledgePurchaseResponseListener {

    private lateinit var mBillingClient: BillingClient
    private var enableDebug: Boolean = true
    private val skusDetails = mutableMapOf<String, SkuDetails?>()

    override fun init() {
        mBillingClient = BillingClient
            .newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()
        mBillingClient.startConnection(this)
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        timber("CHECK THIS BS onBillingSetupFinished invoked billingResult: $billingResult")

        if (billingResult.isOk()) {
            subscriptionSkuKeys.querySkuDetails(BillingClient.SkuType.SUBS) {
                GlobalScope.launch {
                    queryPurchases()
                }
            }
        }
    }

    /**
     * Query Google Play Billing for existing purchases.
     * New purchases will be provided to the PurchasesUpdatedListener.
     */
    private suspend fun queryPurchases() {
        timber("CHECK THIS BS - queryPurchases invoked")
        val subsResult: PurchasesResult = mBillingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS)
        processPurchases(subsResult.purchasesList, isRestore = true)
    }

    override fun subscribe(activity: Activity, sku: String, token: String) {
        timber("CHECK THIS subscribe. invoked sku.isSkuReady ${sku.isSkuReady()}")
        if (!sku.isSkuReady()) {
            return
        }
        launchBillingFlow(activity, sku, token)
    }

    private fun launchBillingFlow(activity: Activity, sku: String, token: String) {
        timber("CHECK THIS launchBillingFlow. invoked - SKU $sku and Token $token")
        sku.toSkuDetails(BillingClient.SkuType.SUBS) { skuDetails ->
            if (skuDetails != null) {
                val purchaseParams = BillingFlowParams
                    .newBuilder()
                    .setSkuDetails(skuDetails)
                    .setObfuscatedAccountId(token)
                    .build()
                mBillingClient.launchBillingFlow(activity, purchaseParams)
            }
        }
    }

    override fun unsubscribe(activity: Activity, sku: String) {
        timber("CHECK THIS unsubscribe sku $sku")
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            val subscriptionUrl = ("http://play.google.com/store/account/subscriptions"
                    + "?package=" + activity.packageName
                    + "&sku=" + sku)
            intent.data = Uri.parse(subscriptionUrl)
            activity.startActivity(intent)
            activity.finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun enableDebugLogging(enable: Boolean) {
        timber("CHECK THIS enableDebugLogging: enable: $enable ")
    }

    /**
     * Called by the Billing Library when new purchases are detected.
     */
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        timber("CHECK THIS onPurchasesUpdated: responseCode:$responseCode debugMessage: $debugMessage")
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                timber("onPurchasesUpdated. purchase: $purchases")
                processPurchases(purchases)
            }
            BillingClient.BillingResponseCode.USER_CANCELED ->
                timber("onPurchasesUpdated: User canceled the purchase")
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                timber("onPurchasesUpdated: The user already owns this item")
                //item already owned? call queryPurchases to verify and process all such items
                GlobalScope.launch {
                    queryPurchases()
                }
            }
            BillingClient.BillingResponseCode.DEVELOPER_ERROR ->
                timber(
                    "onPurchasesUpdated: Developer error means that Google Play " +
                            "does not recognize the configuration. If you are just getting started, " +
                            "make sure you have configured the application correctly in the " +
                            "Google Play Console. The SKU product ID must match and the APK you " +
                            "are using must be signed with release keys."
                )
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
            BillingClient.BillingResponseCode.ERROR -> {
                timber("onSkuDetailsResponse: $responseCode $debugMessage")
            }
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {
                // These response codes are not expected.
                timber("onSkuDetailsResponse: $responseCode $debugMessage")
            }
        }
    }

    private fun processPurchases(purchasesList: List<Purchase>?, isRestore: Boolean = false) {
        if (purchasesList != null) {
            timber("CHECK THIS processPurchases: " + purchasesList.size + " purchase(s)")
            purchases@ for (purchase in purchasesList) {
                // TODO - handle list iteration
                // TODO - handle PurchaseState.PENDING
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && purchase.skus[0].isSkuReady()) {
                    // Grant entitlement to the user.
                    val skuDetails = skusDetails[purchase.skus[0]]
                    timber("CHECK THIS skuDetails: $skuDetails")
                    when (skuDetails?.type) {
                        BillingClient.SkuType.SUBS -> {
                            timber("CHECK THIS subscriptionOwned: $isRestore")
                            subscriptionOwned(getPurchaseInfo(purchase), isRestore)
                        }
                    }

                    // Acknowledge the purchase if it hasn't already been acknowledged.
                    timber("CHECK THIS Acknowledge purchase ${purchase.purchaseToken}")
                    if (!purchase.isAcknowledged) {
                        val acknowledgePurchaseParams = AcknowledgePurchaseParams
                            .newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)
                            .build()
                        mBillingClient.acknowledgePurchase(acknowledgePurchaseParams, this)
                    }
                } else {
                    timber(
                        "processPurchases failed. purchase: $purchase purchaseState: ${purchase.purchaseState} isSkuReady: ${purchase.skus[0].isSkuReady()}"
                    )
                }
            }
        } else {
            timber("processPurchases: with no purchases")
        }
    }

    /**
     * Update Sku details after initialization.
     * This method has cache functionality.
     */
    private fun List<String>.querySkuDetails(type: String, done: () -> Unit) {
        if (::mBillingClient.isInitialized.not() || !mBillingClient.isReady) {
            timber("CHECK THIS querySkuDetails")
            done()
            return
        }

        val params = SkuDetailsParams
            .newBuilder()
            .setSkusList(this)
            .setType(type)
            .build()

        mBillingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            if (billingResult.isOk()) {
                skuDetailsList?.forEach { skusDetails[it.sku] = it }

                skusDetails.mapNotNull { entry ->
                    entry.value?.price?.let { entry.key to it }
                }.let { updatePrices(it.toMap()) }
            }
            done()
        }
    }

    /**
     * Get Sku details by sku and type.
     * This method has cache functionality.
     */
    private fun String.toSkuDetails(type: String, done: (skuDetails: SkuDetails?) -> Unit = {}) {
        if (::mBillingClient.isInitialized.not() || !mBillingClient.isReady) {
            timber("CHECK THIS String toSkuDetails")
            done(null)
            return
        }

        val skuDetailsCached = skusDetails[this]
        if (skuDetailsCached != null) {
            done(skuDetailsCached)
            return
        }

        val params = SkuDetailsParams
            .newBuilder()
            .setSkusList(listOf(this))
            .setType(type)
            .build()

        mBillingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            if (billingResult.isOk()) {
                val skuDetails: SkuDetails? = skuDetailsList?.find { it.sku == this }
                skusDetails[this] = skuDetails
                done(skuDetails)
            } else {
                timber("launchBillingFlow. Failed to get details for sku: $this")
                done(null)
            }
        }
    }

    private fun getPurchaseInfo(purchase: Purchase): DataWrappers.PurchaseInfo {
        return DataWrappers.PurchaseInfo(
            getSkuInfo(skusDetails[purchase.skus[0]]!!),
            purchase.purchaseState,
            purchase.developerPayload,
            purchase.isAcknowledged,
            purchase.isAutoRenewing,
            purchase.orderId,
            purchase.originalJson,
            purchase.packageName,
            purchase.purchaseTime,
            purchase.purchaseToken,
            purchase.signature,
            purchase.skus[0],
            purchase.accountIdentifiers
        )
    }

    private fun getSkuInfo(skuDetails: SkuDetails): DataWrappers.SkuInfo {
        return DataWrappers.SkuInfo(
            skuDetails.sku,
            skuDetails.description,
            skuDetails.freeTrialPeriod,
            skuDetails.iconUrl,
            skuDetails.introductoryPrice,
            skuDetails.introductoryPriceAmountMicros,
            skuDetails.introductoryPriceCycles,
            skuDetails.introductoryPricePeriod,
            skuDetails.originalJson,
            skuDetails.originalPrice,
            skuDetails.originalPriceAmountMicros,
            skuDetails.price,
            skuDetails.priceAmountMicros,
            skuDetails.priceCurrencyCode,
            skuDetails.subscriptionPeriod,
            skuDetails.title,
            skuDetails.type
        )
    }

    private fun String.isSkuReady(): Boolean {
        return skusDetails.containsKey(this) && skusDetails[this] != null
    }

    override fun onBillingServiceDisconnected() {
        timber("CHECK THIS onBillingServiceDisconnected")
    }

    override fun onAcknowledgePurchaseResponse(billingResult: BillingResult) {
        timber("CHECK THIS onAcknowledgePurchaseResponse: billingResult: $billingResult")
    }

    override fun close() {
        mBillingClient.endConnection()
        super.close()
    }

    private fun BillingResult.isOk(): Boolean {
        return this.responseCode == BillingClient.BillingResponseCode.OK
    }

    private fun timber(message: String) {
        if (enableDebug) {
            Timber.d(message)
        }
    }

    companion object {
        const val TAG = "GoogleBillingService"
    }
}