package com.sensibol.lucidmusic.singstr.domain.model

data class Submits(
    val attempt: List<Attempt>
)

data class Attempt(
    val id: String,
    val comment: String,
    val coverUrl: String,
    val publicMediaUrl: String,
    val score: Score,
    val songId: String,
    val songTitle: String
) {
    data class Score(
        val detailScore: DetailScore,
        val total: Float
    ) {
        data class DetailScore(
            val lessonScore: Float,
            val reviewData: String,
            val timingScore: Float,
            val tuneScore: Float
        )
    }
}