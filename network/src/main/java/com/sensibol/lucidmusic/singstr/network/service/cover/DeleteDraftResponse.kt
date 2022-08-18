package com.sensibol.lucidmusic.singstr.network.service.cover

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeleteDraftResponse(
    val success: Boolean,
//    val data: Data,
) {

    @JsonClass(generateAdapter = true)
    class Data()

    // FIXME - parse conditional response, conditioned on 'success' boolean
//    @JsonClass(generateAdapter = true)
//    data class Data(
//        val code: Int,
//        val msg: String
//    )
}



