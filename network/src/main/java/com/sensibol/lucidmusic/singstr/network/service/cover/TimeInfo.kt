package com.sensibol.lucidmusic.singstr.network.service.cover

import com.sensibol.lucidmusic.singstr.domain.model.SingScore
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

internal fun SingScore.toTimeInfo() = TimeInfo(
    songDuration = songDurationMS,
    totalRecordingDuration = totalRecDurationMS,
    singableRecordingDuration = singableRecDurationMS
)
