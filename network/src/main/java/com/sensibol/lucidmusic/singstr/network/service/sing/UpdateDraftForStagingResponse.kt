package com.sensibol.lucidmusic.singstr.network.service.sing

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UpdateDraftForStagingResponse (
    val message: String,
    val data: String
)