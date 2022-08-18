package com.sensibol.lucidmusic.singstr.network.service.sing

import com.sensibol.lucidmusic.singstr.network.service.AUTH_HEADER
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

internal interface RetrofitSingWebService {

    @GET("sing/song/genre")
    suspend fun getGenres(): GetGenreResponse

    @GET("sing/song/one")
    suspend fun getSong(
        @Query("song_id", encoded = true) songId: String
    ): GetSongResponse

    @GET("sing/song")
    suspend fun getSongs(
        @Query("genre", encoded = true) genre: String?,
        @Query("difficulty", encoded = true) difficulty: String?,
        @Query("keyword", encoded = true) query: String?,
    ): GetSongsResponse

    @GET("sing/song/priviewurl")
    suspend fun getSongPreviewUrl(
        @Query("song_id", encoded = true) songId: String?
    ): GetSongPreviewUrlResponse

    @GET("sing/song/recommended")
    suspend fun getRecommendedSong(
        @Header(AUTH_HEADER) token: String,
    ): GetSongsResponse

    @GET("sing/song/trending")
    suspend fun getTrendingSongs(
        @Header(AUTH_HEADER) token: String,
        @Query("genre", encoded = true) genre: String?
    ): GetSongsResponse

    @GET("sing/review/get")
    suspend fun getDetailAnalysis(
        @Header(AUTH_HEADER) token: String,
        @Query("attemptid", encoded = true) attemptId: String?
    ): GetDetailAnalysisResponse

    @GET("sing/review/summary")
    suspend fun getSimpleAnalysis(
        @Header(AUTH_HEADER) token: String,
        @Query("attemptid", encoded = true) attemptId: String?
    ): GetSimpleAnalysisResponse

    @GET("elastic/search")
    suspend fun getSearchResults(
        @Header(AUTH_HEADER) token: String,
        @Query("keyword", encoded = true) keyword: String,
        @Query("lookup", encoded = true) lookup: String,
        @Query("page", encoded = true) page: String,
    ): GetSearchResponse
}