package com.sensibol.lucidmusic.singstr.network.service.sing.detailAnalysis


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DetailScore(
    @Json(name = "lesson_score")
    val lessonScore: Int,
    @Json(name = "reviewData")
    val reviewData: List<ReviewData>,
    @Json(name = "timing_score")
    val timingScore: Double,
    @Json(name = "tune_score")
    val tuneScore: Double
)