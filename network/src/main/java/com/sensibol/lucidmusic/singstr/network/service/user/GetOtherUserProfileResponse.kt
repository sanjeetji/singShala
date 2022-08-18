package com.sensibol.lucidmusic.singstr.network.service.user


import com.sensibol.lucidmusic.singstr.domain.model.OtherUserProfile
import com.sensibol.lucidmusic.singstr.network.data.response.UserDetailResponse
import com.sensibol.lucidmusic.singstr.network.data.response.UserStatsResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetOtherUserProfileResponse.Data.OtherUserProfileData.toOtherUserProfileData() = OtherUserProfile(
    isSubscribed = isSubscribed,
    stats = stats.toUserStats(),
    user = user.toUser()
)


@JsonClass(generateAdapter = true)
internal data class GetOtherUserProfileResponse(
    @Json(name = "data")
    val `data`: Data,
    @Json(name = "success")
    val success: Boolean
) {
    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "data")
        val data: OtherUserProfileData
    ) {
        @JsonClass(generateAdapter = true)
        data class OtherUserProfileData(
            @Json(name = "isSubscribed")
            val isSubscribed: Boolean,
            @Json(name = "stats")
            val stats: UserStatsResponse,
            @Json(name = "user")
            val user: UserDetailResponse
        )
    }
}