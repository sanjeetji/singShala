package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.UserStats
import com.sensibol.lucidmusic.singstr.network.data.response.UserStatsResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun UserStatsResponse.toUserStats() = UserStats(
    viewDurationMS = duration,
    level = level,
    totalXp = xp,
    averageTuneScore = avgTune,
    averageTimeScore = avgTime,
    remainingPracticeTimeMS = remainingPracticeTime,
    subscribersCount = subscribe,
    subscription = subscription,
    coversCount = coversCount,
    nextLevel = nextLevel,
    remainingNextXp = remainingNextXp ,
    draftsCount=draftCount,
    pendingXP = pendingxp
)

@JsonClass(generateAdapter = true)
internal data class GetUserStatsResponse(
    val success: Boolean,
    val data: Data
) {
    @JsonClass(generateAdapter = true)
    internal data class Data(
        @Json(name = "stats")
        val stats: UserStatsResponse
    )
}
