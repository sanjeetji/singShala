package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.CarouselBanner
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun ListCorouselResponse.Data.Carousel.toCorousel() = CarouselBanner(
    id = id,
    thumbnailUrl = thumbnail_url,
    deepLinkUrl = deeplink_url,
    order = order,
    status = status
)

@JsonClass(generateAdapter = true)
internal data class ListCorouselResponse(
    @Json(name = "success")
    val success: Boolean,
    @Json(name = "data")
    val data: Data
) {
    @JsonClass(generateAdapter = true)
    internal data class Data(
        @Json(name = "carousel")
        val carousel: List<Carousel>
    ) {
        @JsonClass(generateAdapter = true)
        internal data class Carousel(
            val id: String,
            val thumbnail_url: String,
            val deeplink_url: String,
            val order: Int,
            val status: Boolean
        )
    }
}
