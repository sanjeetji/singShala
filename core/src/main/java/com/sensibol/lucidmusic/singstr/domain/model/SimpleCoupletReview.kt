package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

open class SimpleCoupletReview(
    open val lyrics: String,
    open val remark: String,
) {
    enum class Remark {
        Good,
        Average,
        Bad,
        None
    }
}

@Parcelize
class SimpleAnalysisSongMini(
    val id: String,
    val title: String,
    val artists: List<Artist>,
    val difficulty: String,
    val thumbnailUrl: String,
    val lyrics: String
) : Parcelable