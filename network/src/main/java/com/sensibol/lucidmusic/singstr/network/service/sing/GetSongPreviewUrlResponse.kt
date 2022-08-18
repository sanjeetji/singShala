package com.sensibol.lucidmusic.singstr.network.service.sing

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GetSongPreviewUrlResponse(
    val data: Data,
    val success: Boolean

) {

    @JsonClass(generateAdapter = true)
    data class Data(
        val url: String
    )

}
