package com.sensibol.lucidmusic.singstr.network.service.curriculum

import com.sensibol.lucidmusic.singstr.domain.model.ExerciseScore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TimeInfo(
    @Json(name = "song_duration")
    val songDuration: Int,

    @Json(name = "total_recording_duration")
    val totalRecordingDuration: Int,

    @Json(name = "singable_recording_duration")
    val singableRecordingDuration: Int
)

internal fun ExerciseScore.toTimeInfo() = TimeInfo(
    songDuration = songDurationMS,
    totalRecordingDuration = totalRecDurationMS,
    singableRecordingDuration = singableRecDurationMS
)
