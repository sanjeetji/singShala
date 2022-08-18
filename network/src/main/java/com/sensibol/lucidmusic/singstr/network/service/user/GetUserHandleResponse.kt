package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.UserHandle
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


internal fun GetUserHandleResponse.toUserHandle() = UserHandle(
    status = status,
    message = message,
    data = userHandle.toData()
)

internal fun GetUserHandleResponse.UserHandle.toData() = UserHandle.Data(
    userHandle = userHandle
)


@JsonClass(generateAdapter = true)
internal data class GetUserHandleResponse (
    @Json(name = "status")
    val status:Int,
    @Json(name = "message")
    val message:String,
    @Json(name = "data")
    val userHandle:UserHandle
){
    @JsonClass(generateAdapter = true)
    data class UserHandle(
        @Json(name = "user_handle")
        val userHandle:String
    )

}