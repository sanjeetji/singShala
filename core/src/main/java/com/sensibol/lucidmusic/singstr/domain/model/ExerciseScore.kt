package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExerciseScore(
    val exerciseId: String,
    val songDurationMS: Int,
    val totalRecDurationMS: Int,
    val singableRecDurationMS: Int,
    val totalScore: Float,
    val tuneScore: Float,
    val timingScore: Float,
    val reviewData: String
) : Parcelable