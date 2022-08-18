package com.sensibol.lucidmusic.singstr.domain.model


data class FollowingFeed(
    val covers: List<CoverData>,
    val nextPageToken: String,
)