package com.sensibol.lucidmusic.singstr.network.service.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UnlockInfo(
    @Json(name = "type")
    val type: String,
    @Json(name = "credit")
    val credit: Int,
    @Json(name = "contest_id")
    val contest_id: String,
    @Json(name = "audition_id")
    val audition_id: String,
    @Json(name = "number_of_review")
    val numberOfReview: Int,
    @Json(name = "validity")
    val validity: Int,
    @Json(name = "expiry")
    val expiry: String
)
