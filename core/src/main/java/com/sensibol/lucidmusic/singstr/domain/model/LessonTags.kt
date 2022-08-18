package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LessonTags(
    val id: String,
    val title: String,
    var isCheck: Boolean = false
) : Parcelable

val allLessons =
    LessonTags("", "ALL LESSONS", true)