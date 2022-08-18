package com.sensibol.lucidmusic.singstr.network.service.content

import retrofit2.http.GET
import retrofit2.http.Query

internal interface RetrofitContentWebService {

    @GET("contentdelivery/song/perform")
    suspend fun getSongContentUrls(
        @Query("song_id") songId: String,
        @Query("media_version") mediaVersion: String,
        @Query("gender") gender: String
    ): GetSongContentResponse

    @Deprecated("lesson id (i.e. level_id) is being unnecessarily passed due to incorrect backend")
    @GET("contentdelivery/level/exercise")
    suspend fun getExerciseContent(
        @Query("level_id") lessonId: String,
        @Query("exercise_id") exerciseId: String,
        @Query("media_version") mediaVersion: String,
        @Query("gender") gender: String
    ): GetSongContentResponse

    @GET("contentdelivery/song/practice")
    suspend fun getSongPracticeContentUrls(
        @Query("song_id") songId: String,
        @Query("media_version") mediaVersion: String
    ): GetSongContentResponse
}