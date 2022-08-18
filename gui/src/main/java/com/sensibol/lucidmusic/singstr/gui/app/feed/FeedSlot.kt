package com.sensibol.lucidmusic.singstr.gui.app.feed

import com.sensibol.lucidmusic.singstr.domain.model.Comment
import com.sensibol.lucidmusic.singstr.domain.model.Cover
import com.sensibol.lucidmusic.singstr.domain.model.LearnSlot

internal abstract class FeedSlot(
    open val videoUrl: String,
    open val thumbnailUrl: String,
)

internal data class CoverSlot(
    override val videoUrl: String,
    val cover: Cover,
    val comments: List<Comment>
) : FeedSlot(videoUrl, cover.thumbnailUrl) {

    override fun toString(): String = "${cover.songMini.title} by ${cover.userMini.handle}"
}

internal data class LearnSlot(
    override val videoUrl: String,
    val learnSlot: LearnSlot
) : FeedSlot(videoUrl, learnSlot.thumbnailUrl) {

    override fun toString(): String = learnSlot.title
}