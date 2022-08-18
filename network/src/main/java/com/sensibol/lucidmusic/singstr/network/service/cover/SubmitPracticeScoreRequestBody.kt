package com.sensibol.lucidmusic.singstr.network.service.cover

import com.sensibol.lucidmusic.singstr.domain.model.SingScore
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubmitPracticeScoreRequestBody(

    @Json(name = "song_id")
    val songId: String,

    @Json(name = "time_info")
    val timeInfo: TimeInfo,

    @Json(name = "score")
    val score: Score,

    @Json(name = "media_type")
    val mediaType: String
)

internal fun SingScore.toSubmitPracticeScoreRequestBody(songMini: SongMini) = SubmitPracticeScoreRequestBody(
    songId = songId,
    timeInfo = toTimeInfo(),
    score = toScore(songMini),
    // FIXME - hard-coded media_type
    mediaType = "Audio",

)
