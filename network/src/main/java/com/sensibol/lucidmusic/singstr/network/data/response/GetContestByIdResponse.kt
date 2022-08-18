package com.sensibol.lucidmusic.singstr.network.data.response
import com.sensibol.lucidmusic.singstr.domain.model.ContestData
import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json


@JsonClass(generateAdapter = true)
data class GetContestByIdResponse(
    @Json(name = "status")
    val status:Int,
    @Json(name = "data")
    val data: ContestDataResponseById,
    @Json(name = "message")
    val message: String
)

