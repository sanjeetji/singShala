package com.sensibol.lucidmusic.singstr.network.service.learn

import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.sensibol.lucidmusic.singstr.domain.model.TeacherSubscribeUnsbscribe
import com.sensibol.lucidmusic.singstr.network.data.response.LessonMiniResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetTeacherSubscribeUnsubscribeResponse.toTeacherSubscribeUnsbscribe() = TeacherSubscribeUnsbscribe(
    success = success,
    data = data.toData()
)

internal fun GetTeacherSubscribeUnsubscribeResponse.Data.toData() = TeacherSubscribeUnsbscribe.Data(
    msg = msg,
    code = code
)

@JsonClass(generateAdapter = true)
internal class GetTeacherSubscribeUnsubscribeResponse(
    @Json(name = "success")
    val success: Boolean,
    @Json(name = "data")
    val data: Data,
) {
    @JsonClass(generateAdapter = true)
    class Data(
        @Json(name = "msg")
        val msg: String,
        @Json(name = "code")
        val code:Int
    )
}