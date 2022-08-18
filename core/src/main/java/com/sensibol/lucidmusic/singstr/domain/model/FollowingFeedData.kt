package com.sensibol.lucidmusic.singstr.domain.model


data class FollowingFeedData(
    val status: Int,
    var message:String,
    val data: FollowingFeed
)

//
//data class FeedData(
//    val currentWatchFeed: String,
//    val nextPageToken: String,
//    val submits: List<Submit>,
//) {
//    data class Submit(
//        val attempt: Attempt,
//        val author: Author,
//        val song: Song,
//        val statistics: Statistics
//    ) {
//        data class Attempt(
//            val coverUrl: String,
//            val id: String,
//            val liked: Boolean,
//            val orderId: String,
//            val score: Score,
//            val time: String
//        ) {
//            data class Score(
//                val detailScore: DetailScore,
//                val total: Double
//            ) {
//
//                data class DetailScore(
//                    val lessonScore: Int,
//                    val reviewData: String,
//                    val timingScore: Int,
//                    val tuneScore: Int
//                )
//            }
//        }
//
//        data class Author(
//            val displayName: String,
//            val id: String,
//            val profileUrl: String
//        )
//
//        data class Song(
//            val album: String,
//            val artists: List<String>,
//            val id: String,
//            val title: String,
//            val year: Int
//        )
//
//        data class Statistics(
//            val commentCount: Int,
//            val likeCount: Int,
//            val playCount: Int,
//            val shareCount: Int
//        )
//    }
//}