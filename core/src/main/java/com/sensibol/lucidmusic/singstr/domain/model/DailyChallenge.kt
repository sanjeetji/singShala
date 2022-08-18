package com.sensibol.lucidmusic.singstr.domain.model

data class DailyChallenge(
    val challengeDate: String,
    val id: String,
    val song: SongMini
)