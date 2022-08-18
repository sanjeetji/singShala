package com.sensibol.lucidmusic.singstr.domain.model

data class CoverData(
    val attempt: Attempt,
    val songMini: SongMini,
    val userMini: UserMini,
    val statistics: Statistics
) {
    data class Attempt(
        var id: String,
        val orderId: String,
        val coverUrl: String,
        var time: String,
        val liked: Boolean,
        val caption: String,
    )
    data class SongMini(
        var id: String,
        val title: String,
        val album: String,
        val year:Int,
        var lyrics: String,
        val lyrics_start_time:Int,
        val artists:List<Artist>,
        val thumbnailUrl: String,
        val difficulty: String,
    ){
        data class Artist(
            var name:String
        )
    }
    data class UserMini(
        var id: String,
        val firstname: String,
        val lastname: String,
        val profileUrl: String,
        var userhandle: String
    )
    data class Statistics(
        var likeCount:Int,
        var commentCount:Int,
        var clapCount: Int,
        val shareCount: Int,
        val viewCount: Int,
    )
}


