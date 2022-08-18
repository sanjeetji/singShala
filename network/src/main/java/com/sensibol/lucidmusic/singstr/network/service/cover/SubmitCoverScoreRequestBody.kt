package com.sensibol.lucidmusic.singstr.network.service.cover

import com.sensibol.lucidmusic.singstr.domain.model.SingScore
import com.sensibol.lucidmusic.singstr.domain.model.Song
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubmitCoverScoreRequestBody(

    @Deprecated("Legacy parameter from TSM")
    @Json(name = "reference_id")
    val referenceId: String,

    @Deprecated("Legacy parameter from TSM")
    @Json(name = "reference_type")
    val referenceType: String = "Song_Perform",

    @Json(name = "time_info")
    val timeInfo: TimeInfo,

    @Json(name = "score")
    val score: Score,

    @Json(name = "media_type")
    val mediaType: String
)

internal fun SingScore.toSubmitCoverScoreRequestBody(songMini: SongMini) = SubmitCoverScoreRequestBody(
    referenceId = songId,
    timeInfo = toTimeInfo(),
    score = toScore(songMini),
    // FIXME - hard-coded media_type
    mediaType = "Audio",

)