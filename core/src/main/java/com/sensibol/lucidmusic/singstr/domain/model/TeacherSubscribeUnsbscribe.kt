package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class TeacherSubscribeUnsbscribe(
    val success: Boolean,
    val data: Data
) {
    data class Data(
        val msg: String,
        val code: Int
    )
}



