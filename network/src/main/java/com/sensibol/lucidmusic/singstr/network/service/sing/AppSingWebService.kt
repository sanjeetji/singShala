package com.sensibol.lucidmusic.singstr.network.service.sing

import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.domain.webservice.SingWebService
import com.sensibol.lucidmusic.singstr.network.data.response.toSong
import com.sensibol.lucidmusic.singstr.network.data.response.toSongMini
import com.sensibol.lucidmusic.singstr.network.service.networkCall
import com.sensibol.lucidmusic.singstr.network.service.toBearerToken
import java.util.*
import javax.inject.Inject

internal class AppSingWebService @Inject constructor(
    private val singService: RetrofitSingWebService,
) : SingWebService {

    override suspend fun getGenres(): List<Genre> = networkCall(
        { singService.getGenres() },
        { response -> response.data.genres.map { it.toGenre() } }
    )


    override suspend fun getSongs(genre: Genre?, difficulty: Song.Difficulty?, query: String?): List<SongMini> = networkCall(
        {
            singService.getSongs(
                genre = genre?.name?.toLowerCase(Locale.ROOT),
                difficulty = difficulty?.name?.toLowerCase(Locale.ROOT),
                query = query
            )
        },
        { response -> response.data.songs.map { it.toSongMini() } }
    )

    override suspend fun getSong(songId: String): Song = networkCall(
        { singService.getSong(songId) },
        { response -> response.data.song.toSong() }
    )

    override suspend fun getSongPreviewUrl(songId: String): String = networkCall(
        { singService.getSongPreviewUrl(songId) },
        { response -> response.data.url }
    )

    override suspend fun getRecommendSongs(authToken: AuthToken): List<SongMini> = networkCall(
        { singService.getRecommendedSong(authToken.toBearerToken()) },
        { response -> response.data.songs.map { it.toSongMini() } }
    )

    override suspend fun getTrendingSongs(authToken: AuthToken, genre: Genre?): List<SongMini> = networkCall(
        { singService.getTrendingSongs(authToken.toBearerToken(), genre?.name?.toLowerCase(Locale.ROOT)) },
        { response -> response.data.songs.map { it.toSongMini() } }
    )

    override suspend fun getDetailAnalysis(authToken: AuthToken, attemptId: String): DetailedAnalysis = networkCall(
        { singService.getDetailAnalysis(authToken.toBearerToken(), attemptId) },
        { response -> response.data.reviewDetail.toReviewDetail() }
    )

    override suspend fun getSimpleAnalysis(authToken: AuthToken, attemptId: String): SimpleAnalysis = networkCall(
        { singService.getSimpleAnalysis(authToken.toBearerToken(), attemptId) },
        { response -> response.data.reviewDetail.toReviewDetail() }
    )

    override suspend fun getSearchResults(authToken: AuthToken, keyword: String, lookup: String, page: String): SearchData = networkCall(
        { singService.getSearchResults(authToken.toBearerToken(), keyword, lookup, page) },
        { response -> response.data.toSearchData() }
    )
}