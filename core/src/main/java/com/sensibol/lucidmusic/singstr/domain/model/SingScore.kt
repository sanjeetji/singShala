package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// FIXME - create another different objects for cover mode and practice mode. File paths could be separated for practice mode.
@Parcelize
data class SingScore(
    val songId: String,
    val songDurationMS: Int,
    val totalRecDurationMS: Int,
    val singableRecDurationMS: Int,
    val totalScore: Float,
    val tuneScore: Float,
    val timingScore: Float,
    val reviewData: String,
    @Deprecated("To be moved out of this object")
    val mixPath: String,
    @Deprecated("To be moved out of this object")
    val metaPath: String,
    @Deprecated("To be moved out of this object")
    val rawRecPath: String,
) : Parcelable
