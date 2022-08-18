package com.sensibol.lucidmusic.singstr.network.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class UserStatsResponse(
    @Json(name = "number_of_cover")
    val coversCount: Int,
    @Json(name = "duration")
    val duration: Long,
    @Json(name = "level")
    val level: Int,
    @Json(name = "nextlevel")
    val nextLevel: Int,
    @Json(name = "xp")
    val xp: Int,
    @Json(name = "remaining_next_xp")
    val remainingNextXp: Int,
    @Json(name = "avg_tune")
    val avgTune: Float,
    @Json(name = "avg_time")
    val avgTime: Float,
    @Json(name = "remaining_practice_time")
    val remainingPracticeTime: Int,
    @Json(name = "subscribe")
    val subscribe: Int,
    @Json(name = "subscription")
    val subscription: Int,
    @Json(name = "draftCount")
    val draftCount: Int,
    @Json(name = "pendingxp")
    val pendingxp: Int

)