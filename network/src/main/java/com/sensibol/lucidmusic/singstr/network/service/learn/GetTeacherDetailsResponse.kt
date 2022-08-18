package com.sensibol.lucidmusic.singstr.network.service.learn


import com.sensibol.lucidmusic.singstr.domain.model.TeacherDetails
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetTeacherDetailsResponse.Data.Attributes.Details.toDetails() = TeacherDetails.Attributes.Details(
    title = title,
    sub_title = sub_title
)

internal fun GetTeacherDetailsResponse.Data.Attributes.toAttributes() = TeacherDetails.Attributes(
    details = details.map { it.toDetails() },
    title = title,
    order = order,
    image = if(image.isNullOrBlank()) "" else image
)

internal fun GetTeacherDetailsResponse.Data.toTeacherDetails() = TeacherDetails(
    id = id,
    userId = userId ?: "",
    name = name,
    age = 25,
    profile_img_url = profileImgUrl,
    experience = experience,
    bio = bio,
    IsLoginUserfollowers = IsLoginUserfollowers,
    attributes = attributes.map { it.toAttributes() }
)

@JsonClass(generateAdapter = true)
data class GetTeacherDetailsResponse(
    @Json(name = "data")
    val data: List<Data>,
    @Json(name = "message")
    val message: String
) {

    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "_id")
        val id: String,
        @Json(name = "user_id")
        val userId: String?,
        @Json(name = "bio")
        val bio: String,
        @Json(name = "name")
        val name: String,
        @Json(name = "profileImgUrl")
        val profileImgUrl: String,
        @Json(name = "experience")
        val experience: String,
//            @Json(name = "follower")
//            val followers: Int,
        @Json(name = "attributes")
        val attributes: List<Attributes>,
        @Json(name = "IsLoginUserfollowers")
        val IsLoginUserfollowers: Boolean
    ) {
        @JsonClass(generateAdapter = true)
        data class Attributes(
            @Json(name = "details")
            val details: List<Details>,
            @Json(name = "title")
            val title: String,
            @Json(name = "order")
            val order: Int,
            @Json(name = "image")
            val image: String?,
        ) {
            @JsonClass(generateAdapter = true)
            data class Details(
                @Json(name = "title")
                val title: String,
                @Json(name = "sub_title")
                val sub_title: String,
            )
        }
    }
}