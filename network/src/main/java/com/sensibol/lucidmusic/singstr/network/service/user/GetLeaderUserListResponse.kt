package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.LeaderboardUser
import com.squareup.moshi.JsonClass

internal fun GetLeaderUserListResponse.Data.LeaderUser.toLeaderUser() = LeaderboardUser(
    id = _id,
    userId = userId,
    firstName = firstName,
    lastName = lastName,
    profileImg = profileImg,
    xp = curxp.toString(),
    level = curlevel,
    rank = rank,
)

@JsonClass(generateAdapter = true)
data class GetLeaderUserListResponse(
    val success: Boolean,
    val data: Data
) {
    @JsonClass(generateAdapter = true)
    data class Data(
        val userlist: List<LeaderUser>
    ) {
        @JsonClass(generateAdapter = true)
        data class LeaderUser(
            val _id: String,
            val userId: String,
            val firstName: String,
            val lastName: String,
            val email: String,
            val gender: String,
            val profileImg: String,
            val age: Int,
            val curxp: Int,
            val curlevel: Int,
            val rank: Int,
            val xptime: String
        )
    }
}