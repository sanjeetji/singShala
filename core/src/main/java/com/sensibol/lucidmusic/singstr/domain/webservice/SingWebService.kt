package com.sensibol.lucidmusic.singstr.domain.webservice

import com.sensibol.lucidmusic.singstr.domain.model.*

interface SingWebService {

    suspend fun getGenres(): List<Genre>

    suspend fun getSongs(genre: Genre? = null, difficulty: Song.Difficulty? = null, query: String? = null): List<SongMini>

    suspend fun getSong(songId: String): Song

    suspend fun getSongPreviewUrl(songId: String): String

    suspend fun getRecommendSongs(authToken: AuthToken): List<SongMini>

    suspend fun getTrendingSongs(authToken: AuthToken, genre: Genre?): List<SongMini>

    suspend fun getDetailAnalysis(authToken: AuthToken, attemptId: String): DetailedAnalysis

    suspend fun getSimpleAnalysis(authToken: AuthToken, attemptId: String): SimpleAnalysis

    suspend fun getSearchResults(authToken: AuthToken, keyword: String, lookup: String, page: String): SearchData

}