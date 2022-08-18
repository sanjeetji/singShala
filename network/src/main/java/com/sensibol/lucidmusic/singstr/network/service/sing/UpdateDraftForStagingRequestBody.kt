package com.sensibol.lucidmusic.singstr.network.service.sing

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UpdateDraftForStagingRequestBody (
    @Json(name = "attempt_id")
    val attemptId: String
)