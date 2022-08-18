package com.sensibol.lucidmusic.singstr.network.data.response

import com.sensibol.lucidmusic.singstr.domain.model.ConceptData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun AllConceptDataInfoResponse.toConceptData() = ConceptData(
    nextPageToken = next_page_token,
    conceptList = concept.map { it.toConceptInfo() }
)

@JsonClass(generateAdapter = true)
 data class AllConceptDataInfoResponse(
    @Json(name = "concept")
    val concept: List<ConceptInfoResponse>,
    @Json(name = "next_page_token")
    val next_page_token: String
)
