package com.sensibol.lucidmusic.singstr.network.data.response

import com.sensibol.lucidmusic.singstr.domain.model.AllConceptData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetAllConceptInfoResponse.toAllConceptData() = AllConceptData(
    success = success,
    data = data.toConceptData()
)

@JsonClass(generateAdapter = true)
class GetAllConceptInfoResponse(
    @Json(name = "success")
    val success: Boolean,

    @Json(name = "data")
    val data: AllConceptDataInfoResponse

)

