package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.SubscriptionPlan
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetSubscriptionPlanResponse.Data.SubscriptionPlanResponse.toSubscriptionPlan() = SubscriptionPlan(
    id = id,
    name = name,
    displayName = displayName,
    type = type,
    order = order,
    discountPrice = discountPrice,
    originalPrice = originalPrice,
    store = store,
    currency = currency,
    img = img,
    desc = desc,
    freeTrialDesc = freeTrialDesc,
    unlockInfo = unlockInfo.map { it.toUnlockInfo() }
)

internal fun UnlockInfo.toUnlockInfo() = SubscriptionPlan.UnlockInfo(
    type = type,
    credit = credit,
    contest_id = contest_id,
    audition_id = audition_id,
    numberOfReview = numberOfReview,
    validity = validity,
    expiry = expiry
)

@JsonClass(generateAdapter = true)
internal data class GetSubscriptionPlanResponse(
    val success: Boolean,
    val data: Data
) {
    @JsonClass(generateAdapter = true)
    internal data class Data(
        @Json(name = "products")
        val products: List<SubscriptionPlanResponse>
    ) {
        @JsonClass(generateAdapter = true)
        internal data class SubscriptionPlanResponse(
            @Json(name = "id")
            val id: String,
            @Json(name = "name")
            val name: String,
            @Json(name = "display_name")
            val displayName: String,
            @Json(name = "type")
            val type: String,
            @Json(name = "order")
            val order: Int,
            @Json(name = "discounted_price")
            val discountPrice: Int,
            @Json(name = "original_price")
            val originalPrice: Int,
            @Json(name = "store")
            val store: String,
            @Json(name = "currency")
            val currency: String,
            @Json(name = "img")
            val img: String,
            @Json(name = "desc")
            val desc: String,
            @Json(name = "free_trial_desc")
            val freeTrialDesc: String,
            @Json(name = "unlock_info")
            val unlockInfo: List<UnlockInfo>
        )
    }
}