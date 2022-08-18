package com.sensibol.lucidmusic.singstr.gui.app.payment

import android.app.Activity
import android.content.Context

class IapConnector @JvmOverloads constructor(
    context: Context,
    subscriptionKeys: List<String> = emptyList(),
    enableLogging: Boolean = false
) {

    private var mBillingService: IBillingService? = null

    init {
        val contextLocal = context.applicationContext ?: context
        mBillingService = BillingService(contextLocal, subscriptionKeys)
        getBillingService().init()
        getBillingService().enableDebugLogging(enableLogging)
    }

    fun addSubscriptionListener(subscriptionServiceListener: SubscriptionServiceListener) {
        getBillingService().addSubscriptionListener(subscriptionServiceListener)
    }

    fun removeSubscriptionListener(subscriptionServiceListener: SubscriptionServiceListener) {
        getBillingService().removeSubscriptionListener(subscriptionServiceListener)
    }

    fun subscribe(activity: Activity, sku: String, token: String) {
        getBillingService().subscribe(activity, sku, token)
    }

    fun unsubscribe(activity: Activity, sku: String) {
        getBillingService().unsubscribe(activity, sku)
    }

    fun destroy() {
        getBillingService().close()
    }

    fun getBillingService(): IBillingService {
        return mBillingService ?: let {
            throw RuntimeException("Call IapConnector to initialize billing service")
        }
    }
}