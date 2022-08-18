package com.sensibol.lucidmusic.singstr.network.data.response
import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.sensibol.lucidmusic.singstr.domain.model.MyLessonMini
import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json


internal fun MyListLessonMini.toLessonMini() = MyLessonMini(
    displayOrder = lessonInfoResponse.order,
    id = lessonId,
    title = lessonInfoResponse.displayName,
    type = lessonInfoResponse.type,
    difficulty = lessonInfoResponse.difficulty,
    subscriptionType = lessonInfoResponse.subscriptionPurchaseType,
    // no need of duration so adding date and time
    addedTime = time,
    thumbnailUrl = lessonInfoResponse.videoInfo.thumbnailUrl,
    videoUrl = lessonInfoResponse.videoInfo.videoUrl
)

@JsonClass(generateAdapter = true)
data class MyListLessonMini (
    @Json(name = "id")
    val id: String,
    @Json(name = "lesson_id")
    val lessonId: String,
    @Json(name = "time")
    val time: String,
    @Json(name = "lesson")
    val lessonInfoResponse: LessonInfoResonse,
)