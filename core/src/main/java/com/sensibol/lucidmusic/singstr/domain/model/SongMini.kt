package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class SongMini(
    open val id: String,
    open val order: Int,
    open val title: String,
    open val artists: List<Artist>,
    open val difficulty: String,
    open val thumbnailUrl: String,
    open val isPracticable: Boolean,
    open val lyricsStartTimeMS: Int,
    open val lyrics: String,
) : Parcelable{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SongMini

        if (id != other.id) return false
        if (order != other.order) return false
        if (title != other.title) return false
        if (artists != other.artists) return false
        if (difficulty != other.difficulty) return false
        if (thumbnailUrl != other.thumbnailUrl) return false
        if (isPracticable != other.isPracticable) return false
        if (lyricsStartTimeMS != other.lyricsStartTimeMS) return false
        if (lyrics != other.lyrics) return false
        return true
    }
}