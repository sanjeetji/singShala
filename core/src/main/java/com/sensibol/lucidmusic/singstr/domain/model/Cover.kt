package com.sensibol.lucidmusic.singstr.domain.model

data class Cover(
    val id: String,
    val caption: String,
    val thumbnailUrl: String,
    val songMini: SongMini,
    val userMini: UserMini,
    val statistics: Statistics
) {
    data class Statistics(
        var clapCount: Long,
        val shareCount: Long,
        val viewCount: Long,
    )
}
