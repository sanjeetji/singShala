package com.sensibol.lucidmusic.singstr.network.service.user


import com.sensibol.lucidmusic.singstr.domain.model.DailyChallenge
import com.sensibol.lucidmusic.singstr.network.data.response.SongMiniResponse
import com.sensibol.lucidmusic.singstr.network.data.response.toSongMini
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetDailyChallengeResponse.Data.Challenge.toDailyChallenges() = DailyChallenge(
    id = id,
    challengeDate = challengeDate,
    song = song.toSongMini()
)


@JsonClass(generateAdapter = true)
internal data class GetDailyChallengeResponse(
    @Json(name = "data")
    val `data`: Data,
    @Json(name = "success")
    val success: Boolean
) {
    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "challenge")
        val challenge: Challenge
    ) {
        @JsonClass(generateAdapter = true)
        data class Challenge(
            @Json(name = "challenge_date")
            val challengeDate: String,
            @Json(name = "id")
            val id: String,
            @Json(name = "song")
            val song: SongMiniResponse
        )
    }
}