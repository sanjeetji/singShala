package com.sensibol.lucidmusic.singstr.network.service.streak

import com.sensibol.lucidmusic.singstr.domain.model.StreakInfo
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetStreakInfoResponse.GetStreakInfoData.toStreakInfo() = StreakInfo(
    day = streakDay,
    xp = earnedXp
)

@JsonClass(generateAdapter = true)
internal data class GetStreakInfoResponse(
    @Json(name = "message")
    val message: String,

    @Json(name = "data")
    val data: GetStreakInfoData
){
    @JsonClass(generateAdapter = true)
    data class GetStreakInfoData(

        @Json(name = "day")
        val streakDay: Int,

        @Json(name = "xp")
        val earnedXp: Int
    )
}
