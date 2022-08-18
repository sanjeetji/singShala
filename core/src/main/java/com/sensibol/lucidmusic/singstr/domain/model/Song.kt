package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class Song(
    override val id: String,
    override val order: Int,
    override val title: String,
    override val artists: List<Artist>,
    override val difficulty: String,
    override val thumbnailUrl: String,
    override val isPracticable: Boolean,
    override val lyricsStartTimeMS: Int,
    override val lyrics: String,
) : SongMini(id, order, title, artists, difficulty, thumbnailUrl, isPracticable, lyricsStartTimeMS, lyrics),
    Parcelable {

    enum class Difficulty {
        EASY,
        MEDIUM,
        HARD
    }
}
