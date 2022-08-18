package com.sensibol.lucidmusic.singstr.network.service.sing


import com.sensibol.lucidmusic.singstr.domain.model.SimpleAnalysis
import com.sensibol.lucidmusic.singstr.domain.model.SimpleAnalysisSongMini
import com.sensibol.lucidmusic.singstr.domain.model.SimpleCoupletReview
import com.sensibol.lucidmusic.singstr.network.data.response.ArtistResponse
import com.sensibol.lucidmusic.singstr.network.data.response.toArtist
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


internal fun GetSimpleAnalysisResponse.Data.ReviewDetail.toReviewDetail() = SimpleAnalysis(
    songMini = song.toSongMini(),
    linesNeedsImprovement = linesNeedImprovement,
    linesDonePerfectly = linesDonePerfectly,
    totalScore = score.total.toInt(),
    timeScore = score.detailScore.timingScore.toInt(),
    tuneScore = score.detailScore.tuneScore.toInt(),
    attemptXp = attemptXp.toInt(),
    attemptId = attemptId,
    recordedOn = time,
    simpleCoupletReviews = score.detailScore.reviewData.map { it.toReviewData() },
    viewsCount = viewsCount
)

internal fun GetSimpleAnalysisResponse.Data.ReviewDetail.Score.DetailScore.ReviewData.toReviewData() = SimpleCoupletReview(
    lyrics = lyrics,
    remark = review
)

internal fun GetSimpleAnalysisResponse.Data.ReviewDetail.Song.toSongMini() = SimpleAnalysisSongMini(
    id = id,
    title = title,
    artists = artists.map { it.toArtist() },
    difficulty = difficulty,
    thumbnailUrl = thumbnailUrl,
    lyrics = lyrics,
)


@JsonClass(generateAdapter = true)
data class GetSimpleAnalysisResponse(
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
            @Json(name = "attempt_id")
            val attemptId: String,
            @Json(name = "attemptXp")
            val attemptXp: Double,
            @Json(name = "author")
            val author: Author,
            @Json(name = "_id")
            val id: String,
            @Json(name = "lines_done_perfectly")
            val linesDonePerfectly: Int,
            @Json(name = "lines_need_improvement")
            val linesNeedImprovement: Int,
            @Json(name = "score")
            val score: Score,
            @Json(name = "song")
            val song: Song,
            @Json(name = "time")
            val time: String,
            @Json(name = "total_line_reviewed")
            val totalLineReviewed: Int,
            @Json(name = "user_id")
            val userId: String,
            @Json(name = "viewsCount")
            val viewsCount: Int
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
                    @Json(name = "timing_score")
                    val timingScore: Double,
                    @Json(name = "tune_score")
                    val tuneScore: Double
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
                        val tuneReview: String
                    )
                }
            }

            @JsonClass(generateAdapter = true)
            data class Author(
                @Json(name = "display_name")
                val displayName: String,
                @Json(name = "id")
                val id: String,
                @Json(name = "profile_url")
                val profileUrl: String,
                @Json(name = "user_handle")
                val userHandle: String
            )

            @JsonClass(generateAdapter = true)
            data class Song(
                @Json(name = "id")
                val id: String,
                @Json(name = "difficulty")
                val difficulty: String,
                @Json(name = "artists")
                val artists: List<ArtistResponse>,
                @Json(name = "lyrics")
                val lyrics: String,
                @Json(name = "thumbnail_url")
                val thumbnailUrl: String,
                @Json(name = "title")
                val title: String,
            )
        }
    }
}

