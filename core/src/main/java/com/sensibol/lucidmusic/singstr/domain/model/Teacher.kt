package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Teacher(
    val id: String,
    val name: String,
    val profileImgUrl: String,
): Parcelable