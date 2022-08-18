package com.sensibol.lucidmusic.singstr.network.service.sing.detailAnalysis


import com.sensibol.lucidmusic.singstr.network.service.sing.detailAnalysis.ComponentScores
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReviewData(
    @Json(name = "componentScores")
    val componentScores: ComponentScores,
    @Json(name = "endTimeMilliSec")
    val endTimeMilliSec: Int,
    @Json(name = "lyrics")
    val lyrics: String,
    @Json(name = "review")
    val review: Int,
    @Json(name = "review_category")
    val reviewCategory: String,
    @Json(name = "score")
    val score: Double,
    @Json(name = "startTimeMilliSec")
    val startTimeMilliSec: Int,
    @Json(name = "teacher_comment")
    val teacherComment: String,
    @Json(name = "timing_review")
    val timingReview: String,
    @Json(name = "tune_review")
    val tuneReview: String
)