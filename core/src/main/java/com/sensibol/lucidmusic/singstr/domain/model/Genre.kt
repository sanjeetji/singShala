package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Genre(
    val id: String,
    val language: String,
    val name: String,
    val thumbnail: String,
) : Parcelable

val allGenre =
    Genre("", "All", "All", "")

val contestGenre =
    Genre("", "All", "Contest", "")

val trendingGenre =
    Genre("", "All", "Trending", "")