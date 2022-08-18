package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.Singup
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetSingupUserResponse.toSingup() = Singup(
    status = status,
    message = message,
    data = signupDetail.toUserDetail()
)

internal fun GetSingupUserResponse.SingupDetail.toUserDetail() = Singup.UserDetail(
    id = id,
    loginType = loginType,
    socialId = socialId,
    email = email,
    password = password,
    firstName = firstName,
    lastName = lastName,
    contactNumber = contactNumber,
    profileImg = profileImg,
    status = status,
    userHandle = userHandle,
    displayName = displayName,
    sex = sex,
    devices = devices.map { it.todevice() }
)
internal fun GetSingupUserResponse.SingupDetail.Device.todevice() = Singup.UserDetail.device(
    accessToken = accessToken
)


@JsonClass(generateAdapter = true)
internal data class GetSingupUserResponse (
    @Json(name = "status")
    val status:Int,
    @Json(name = "message")
    val message:String,
    @Json(name = "data")
    val signupDetail:SingupDetail
){
    @JsonClass(generateAdapter = true)
    data class SingupDetail(
        @Json(name = "_id")
        val id:String,
        @Json(name = "login_type")
        val loginType:Int,
        @Json(name = "social_id")
        val socialId:String,
        @Json(name = "email")
        val email:String,
        @Json(name = "password")
        val password:String,
        @Json(name = "first_name")
        val firstName:String,
        @Json(name = "last_name")
        val lastName:String,
        @Json(name = "contact_number")
        val contactNumber:String,
        @Json(name = "profile_img")
        val profileImg:String,
        @Json(name = "status")
        val status:Int,
        @Json(name = "user_handle")
        val userHandle:String,
        @Json(name = "display_name")
        val displayName:String,
        @Json(name = "sex")
        val sex:Int,
        @Json(name = "devices")
        val devices:List<Device>
    ){
        @JsonClass(generateAdapter = true)
        data class Device(
            @Json(name = "access_token")
            val accessToken:String
        )
    }

}