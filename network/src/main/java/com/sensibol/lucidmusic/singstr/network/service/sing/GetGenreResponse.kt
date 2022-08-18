package com.sensibol.lucidmusic.singstr.network.service.sing


import com.sensibol.lucidmusic.singstr.domain.model.Genre
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetGenreResponse.Data.Genre.toGenre() = Genre(
    id = id,
    language = language,
    thumbnail = thumbnail,
    name = name
)

@JsonClass(generateAdapter = true)
data class GetGenreResponse(
    @Json(name = "data")
    val data: Data,
    @Json(name = "success")
    val success: Boolean
) {
    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "genre")
        val genres: List<Genre>
    ) {
        @JsonClass(generateAdapter = true)
        data class Genre(
            @Json(name = "id")
            val id: String,
            @Json(name = "language")
            val language: String,
            @Json(name = "name")
            val name: String,
            @Json(name = "thumbnail")
            val thumbnail: String
        )
    }
}