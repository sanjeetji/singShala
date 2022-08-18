package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class Lesson(
    override val displayOrder: Int,
    override val id: String,
    override val title: String,
    override val type: String,
    override val difficulty: String,
    override val subscriptionType: String,
    override val duration: String,
    override val thumbnailUrl: String,
    override val videoUrl: String,
    val description: String,
    val exercises: List<Exercise>,
    val relatedSongs: List<SongMini>,
    val teacher: Teacher,
    val nextLesson: LessonMini,
    val isAddedToMyList:Boolean
) : LessonMini(displayOrder, id, title, type, difficulty, subscriptionType, duration, thumbnailUrl, videoUrl), Parcelable

