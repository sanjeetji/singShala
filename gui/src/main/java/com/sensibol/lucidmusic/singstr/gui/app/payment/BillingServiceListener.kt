package com.sensibol.lucidmusic.singstr.gui.app.payment

interface BillingServiceListener {
    fun onPricesUpdated(iapKeyPrices: Map<String, String>)
}