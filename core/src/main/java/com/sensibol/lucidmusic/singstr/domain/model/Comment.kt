package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comment(
    val id: String,
    val comment: String,
    val timestamp: Long,
    val userMini: UserMini
) : Parcelable