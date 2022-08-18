package com.sensibol.lucidmusic.singstr.domain.model

data class Draft(
    val song: SongMini,
    val attemptId: String,
    val userMini: UserMini,
    val timeStamp: String,
    val totalXP: Double,
    val thumbnailUrl: String,
)
