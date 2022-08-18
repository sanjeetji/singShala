package com.sensibol.lucidmusic.singstr.network.service.sing.detailAnalysis


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TimeInfo(
    @Json(name = "singable_recording_duration")
    val singableRecordingDuration: Int,
    @Json(name = "song_duration")
    val songDuration: Int,
    @Json(name = "total_recording_duration")
    val totalRecordingDuration: Int
)