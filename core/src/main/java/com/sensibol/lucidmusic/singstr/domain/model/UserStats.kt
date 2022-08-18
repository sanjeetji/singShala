package com.sensibol.lucidmusic.singstr.domain.model

data class UserStats(
    val averageTuneScore: Float,
    val averageTimeScore: Float,
    val viewDurationMS: Long,
    val level: Int,
    val totalXp: Int,
    val subscribersCount: Int,
    val remainingPracticeTimeMS: Int,
    val subscription: Int,
    val remainingNextXp: Int,
    val nextLevel: Int,
    val coversCount: Int,
    val draftsCount:Int,
    val pendingXP:Int
)
