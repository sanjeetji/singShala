package com.sensibol.lucidmusic.singstr.gui.app.payment

interface SubscriptionServiceListener : BillingServiceListener {

    fun onSubscriptionRestored(purchaseInfo: DataWrappers.PurchaseInfo)

    fun onSubscriptionPurchased(purchaseInfo: DataWrappers.PurchaseInfo)
}