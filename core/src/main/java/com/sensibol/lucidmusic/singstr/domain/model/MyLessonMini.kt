package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class MyLessonMini(
    open val displayOrder: Int,
    open val id: String,
    open val title: String,
    open val type: String,
    open val difficulty: String,
    open val subscriptionType: String,
    open val addedTime: String,
    open val thumbnailUrl: String,
    open val videoUrl: String,
) : Parcelable