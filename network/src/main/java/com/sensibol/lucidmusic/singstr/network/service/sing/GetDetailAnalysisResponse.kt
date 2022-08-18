package com.sensibol.lucidmusic.singstr.network.service.sing


import com.sensibol.lucidmusic.singstr.domain.model.DetailedAnalysis
import com.sensibol.lucidmusic.singstr.domain.model.DetailedCoupletReview
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


internal fun GetDetailAnalysisResponse.Data.ReviewDetail.toReviewDetail() = DetailedAnalysis(
    detailedCoupletReviews = score.detailScore.reviewData.map { it.toReviewData() }
)

internal fun GetDetailAnalysisResponse.Data.ReviewDetail.Score.DetailScore.ReviewData.toReviewData() = DetailedCoupletReview(
    lyrics = lyrics,
    remark = review,
    reviewComment = teacherComment,
    startTimeMS = startTimeMilliSec,
    endTimeMS = endTimeMilliSec
)

@JsonClass(generateAdapter = true)
data class GetDetailAnalysisResponse(
    @Json(name = "data")
    val `data`: Data,
    @Json(name = "success")
    val success: Boolean
) {
    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "reviewDetail")
        val reviewDetail: ReviewDetail
    ) {

        @JsonClass(generateAdapter = true)
        data class ReviewDetail(
            @Json(name = "_id")
            val id: String,
            @Json(name = "score")
            val score: Score,
        ) {
            @JsonClass(generateAdapter = true)
            data class Score(
                @Json(name = "detail_score")
                val detailScore: DetailScore,
                @Json(name = "total")
                val total: Double
            ) {
                @JsonClass(generateAdapter = true)
                data class DetailScore(
                    @Json(name = "reviewData")
                    val reviewData: List<ReviewData>,
                ) {
                    @JsonClass(generateAdapter = true)
                    data class ReviewData(
                        @Json(name = "endTimeMilliSec")
                        val endTimeMilliSec: Int,
                        @Json(name = "lyrics")
                        val lyrics: String,
                        @Json(name = "review")
                        val review: String,
                        @Json(name = "score")
                        val score: Double,
                        @Json(name = "startTimeMilliSec")
                        val startTimeMilliSec: Int,
                        @Json(name = "teacher_comment")
                        val teacherComment: String,
                        @Json(name = "timing_review")
                        val timingReview: String,
                        @Json(name = "tune_review")
                        val tuneReview: String,
                        @Json(name = "comment_category")
                        val commentCategory: String,
                    )
                }
            }
        }
    }
}

