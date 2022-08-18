package com.sensibol.lucidmusic.singstr.network.service.sing.detailAnalysis


import com.sensibol.lucidmusic.singstr.network.service.sing.detailAnalysis.DetailScore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Score(
    @Json(name = "detail_score")
    val detailScore: DetailScore,
    @Json(name = "total")
    val total: Double
)