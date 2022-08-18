package com.sensibol.lucidmusic.singstr.network.service.user


import com.sensibol.lucidmusic.singstr.domain.model.ProSubscription
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetUserSubscriptionResponse.Data.Subscription.toSubscription() = ProSubscription(
    subscribed = subscribed,
    validity = validity
)


@JsonClass(generateAdapter = true)
data class GetUserSubscriptionResponse(
    @Json(name = "data")
    val `data`: Data,
    @Json(name = "success")
    val success: Boolean
) {
    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "subscription")
        val subscription: Subscription
    ) {
        @JsonClass(generateAdapter = true)
        data class Subscription(
            @Json(name = "subscribed")
            val subscribed: Boolean,
            @Json(name = "validity")
            val validity: String
        )
    }
}

