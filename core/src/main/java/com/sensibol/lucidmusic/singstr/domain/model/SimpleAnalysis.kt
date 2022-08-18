package com.sensibol.lucidmusic.singstr.domain.model

data class SimpleAnalysis(
    val songMini: SimpleAnalysisSongMini,
    val linesNeedsImprovement: Int,
    val linesDonePerfectly: Int,
    val totalScore: Int,
    val timeScore: Int,
    val tuneScore: Int,
    val attemptXp: Int,
    val recordedOn: String,
    val attemptId: String,
    val simpleCoupletReviews: List<SimpleCoupletReview>,
    val viewsCount: Int
)
