package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contest(
    val data: List<ContestData>,
    val message: String,
    val status: Int
):Parcelable