package com.sensibol.lucidmusic.singstr.network.service.sing.detailAnalysis


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ComponentScores(
    @Json(name = "bonus")
    val bonus: Int,
    @Json(name = "expression")
    val expression: Expression,
    @Json(name = "rhythm")
    val rhythm: Double,
    @Json(name = "sur")
    val sur: Double,
    @Json(name = "tfa")
    val tfa: Double
)