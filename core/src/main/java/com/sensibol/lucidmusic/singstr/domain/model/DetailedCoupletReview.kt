package com.sensibol.lucidmusic.singstr.domain.model

data class DetailedCoupletReview(
    override val lyrics: String,
    override val remark: String,
    val reviewComment: String,
    val startTimeMS: Int,
    val endTimeMS: Int,
) : SimpleCoupletReview(lyrics, remark)
