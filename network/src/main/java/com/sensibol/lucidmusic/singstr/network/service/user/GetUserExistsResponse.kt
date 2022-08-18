package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.CheckUserExists
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


internal fun GetUserExistsResponse.toCheckUserExists() = CheckUserExists(
    status = status,
    message = message,
    data = userExist.toData()
)

internal fun GetUserExistsResponse.UserExist.toData() = CheckUserExists.Data(
    userExists = userExists
)


@JsonClass(generateAdapter = true)
internal data class GetUserExistsResponse (
    @Json(name = "status")
    val status:Int,
    @Json(name = "message")
    val message:String,
    @Json(name = "data")
    val userExist:UserExist
){
    @JsonClass(generateAdapter = true)
    data class UserExist(
        @Json(name = "user_exists")
        val userExists:Boolean
    )

}