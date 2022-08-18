package com.sensibol.lucidmusic.singstr.domain.model

data class CarouselBanner(
    val id: String,
    val thumbnailUrl: String,
    val deepLinkUrl: String,
    val order: Int,
    val status: Boolean
)
