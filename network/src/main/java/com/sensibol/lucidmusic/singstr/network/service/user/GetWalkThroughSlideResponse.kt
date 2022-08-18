package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.AppWalkThroughSlide
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetWalkThroughSlideResponse.WalkThroughSlides.WalkThrough.toWalkThrough() = AppWalkThroughSlide(
    id = id,
    title = title,
    description = subTitle,
    imageUrl = thumbnailUrl
)

@JsonClass(generateAdapter = true)
data class GetWalkThroughSlideResponse(
    @Json(name = "success")
    val success: Boolean,
    @Json(name = "data")
    val data: WalkThroughSlides
) {
    @JsonClass(generateAdapter = true)
    data class WalkThroughSlides(
        @Json(name = "walkthrough")
        val walkthrough: List<WalkThrough>
    ) {
        @JsonClass(generateAdapter = true)
        data class WalkThrough(
            @Json(name = "id")
            val id: String,
            @Json(name = "title")
            val title: String,
            @Json(name = "sub_title")
            val subTitle: String,
            @Json(name = "order")
            val order: Int,
            @Json(name = "published")
            val published: Boolean,
            @Json(name = "thumbnail_url")
            val thumbnailUrl: String
        )
    }
}
