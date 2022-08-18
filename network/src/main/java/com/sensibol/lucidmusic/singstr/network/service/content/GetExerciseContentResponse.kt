package com.sensibol.lucidmusic.singstr.network.service.content

import com.sensibol.lucidmusic.singstr.domain.model.ContentUrls
import com.squareup.moshi.JsonClass

internal fun GetExerciseContentResponse.Data.Url.toContentUrls() = ContentUrls(
    mediaUrl = media_url,
    mediaHash = media_hash,
    metadataUrl = metadata_url,
    metadataHash = metadata_hash
)

@JsonClass(generateAdapter = true)
internal data class GetExerciseContentResponse(
    val success: Boolean,
    val data: Data
) {
    @JsonClass(generateAdapter = true)
    internal data class Data(
        val url: Url,
    ) {
        @JsonClass(generateAdapter = true)
        internal data class Url(
            val media_url: String,
            val media_hash: String,
            val metadata_url: String,
            val metadata_hash: String,
        )
    }
}
