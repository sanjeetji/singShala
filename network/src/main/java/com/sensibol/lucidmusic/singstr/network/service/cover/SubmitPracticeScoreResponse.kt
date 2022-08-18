package com.sensibol.lucidmusic.singstr.network.service.cover

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SubmitPracticeScoreResponse(
    @Json(name = "success")
    val success: Boolean,

    @Json(name = "data")
    val data: Data

) {

    @JsonClass(generateAdapter = true)
    internal class Data()
}
