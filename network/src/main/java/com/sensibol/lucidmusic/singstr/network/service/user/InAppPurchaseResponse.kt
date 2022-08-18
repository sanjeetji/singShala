package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.Purchase
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun InAppPurchaseResponse.Data.GetPurchaseList.toPurchaseList() = Purchase(
    id = id,
    userId = userId,
    emailId = emailId,
    firstName = firstName,
    lastName = lastName,
    productId = productId,
    productPrice = productPrice,
    productName = productName,
    productDesc = productDesc,
    orderId = orderId,
    platform = platform,
    purchaseTime = purchaseTime,
    status = status,
    mode = mode,
    initTime = initTime,
    completeTime = completeTime,
    lastUpdate = lastUpdate,
    unlockInfo = unlockInfo.map { it.toUnlockInfoList() }
)

internal fun UnlockInfo.toUnlockInfoList() = Purchase.UnlockInfo(
    type = type,
    credit = credit,
    contest_id = contest_id,
    audition_id = audition_id,
    numberOfReview = numberOfReview,
    validity = validity,
    expiry = expiry
)

@JsonClass(generateAdapter = true)
internal data class InAppPurchaseResponse(
    val success: Boolean,
    val data: Data
) {
    @JsonClass(generateAdapter = true)
    internal data class Data(
        val purchases: List<GetPurchaseList>
    ) {
        @JsonClass(generateAdapter = true)
        internal data class GetPurchaseList(
            @Json(name = "id")
            val id: String,
            @Json(name = "user_id")
            val userId: String,
            @Json(name = "email_id")
            val emailId: String,
            @Json(name = "first_name")
            val firstName: String,
            @Json(name = "last_name")
            val lastName: String,
            @Json(name = "product_id")
            val productId: String,
            @Json(name = "product_price")
            val productPrice: String,
            @Json(name = "product_name")
            val productName: String,
            @Json(name = "product_description")
            val productDesc: String,
            @Json(name = "order_id")
            val orderId: String,
            @Json(name = "platform")
            val platform: String,
            @Json(name = "purchase_time")
            val purchaseTime: String,
            @Json(name = "status")
            val status: String,
            @Json(name = "mode")
            val mode: String,
            @Json(name = "init_time")
            val initTime: String,
            @Json(name = "complete_time")
            val completeTime: String,
            @Json(name = "last_updated")
            val lastUpdate: String,
            @Json(name = "unlock_info")
            val unlockInfo: List<UnlockInfo>
        )
    }
}