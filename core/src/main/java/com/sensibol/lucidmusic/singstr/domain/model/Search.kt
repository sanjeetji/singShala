package com.sensibol.lucidmusic.singstr.domain.model


data class SearchData(
    val lesson: List<Lesson>,
    val song: List<Song>,
    val user: List<User>
) {
    data class Lesson(
        val difficulty: String,
        val id: String,
        val primaryTag: String,
        val status: String,
        val subscriptionPurchaseType: String,
        val tags: List<String>,
        val teacherName: String,
        val thumbnailUrl: String,
        val title: String,
        val videoName: String
    )

    data class Song(
        val album: String,
        val artists: List<String>,
        val difficulty: String,
        val duration: String,
        val genres: List<String>,
        val id: String,
        val status: String,
        val thumbnailUrl: String,
        val title: String
    )

    data class User(
        val firstName: String,
        val gender: String,
        val id: String,
        val lastName: String,
        val profileImg: String,
        val userHandle: String
    )
}
