package com.sensibol.lucidmusic.singstr.network.service.feed

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class DeleteCoverRequestBody(
    val attemptId: String,
)