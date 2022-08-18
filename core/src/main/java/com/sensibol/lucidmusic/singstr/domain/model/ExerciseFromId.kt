package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class ExerciseFromId (
   val exercise: List<Exercise>,
    val lessonId: String
)