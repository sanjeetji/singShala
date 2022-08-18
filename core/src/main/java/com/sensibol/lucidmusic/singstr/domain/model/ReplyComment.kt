package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class ReplyComment(
    val status: Int,
    val message: String,
    val data: List<AllReplyComment>
)