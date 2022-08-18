package com.sensibol.lucidmusic.singstr.network.data.response

import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun ConceptLessonMiniResponse.toLessonMini() = LessonMini(
    displayOrder = order,
    id = id,
    title = displayName,
    type = primaryTag,
    difficulty = difficulty,
    subscriptionType = subscriptionType,
    duration = "",
    thumbnailUrl = videoInfoResponse.thumbnailUrl,
    videoUrl = videoInfoResponse.videoUrl
)

@JsonClass(generateAdapter = true)
class ConceptLessonMiniResponse(
    @Json(name = "order")
    val order: Int,

    @Json(name = "_id")
    val id: String,

    @Json(name = "display_name")
    val displayName: String,

    @Json(name = "primary_tag")
    val primaryTag: String,

    @Json(name = "subscription_purchase_type")
    val subscriptionType: String,

    @Json(name = "difficulty")
    val difficulty: String,

    @Json(name = "video")
    val videoInfoResponse: ConceptVideoInfoResponse
)

