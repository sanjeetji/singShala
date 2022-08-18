package com.sensibol.lucidmusic.singstr.network.service.content

import com.sensibol.lucidmusic.singstr.domain.model.ContentUrls
import com.sensibol.lucidmusic.singstr.domain.webservice.ContentWebService
import com.sensibol.lucidmusic.singstr.network.service.networkCall
import javax.inject.Inject

internal class AppContentWebService @Inject constructor(
    private val contentService: RetrofitContentWebService,
) : ContentWebService {


    override suspend fun getSongContentUrls(songId: String): ContentUrls = networkCall(
        { contentService.getSongContentUrls(songId, "1.0", "Male") },
        { it.data.url.toContentUrls() }
    )

    override suspend fun getExerciseContent(lessonId: String, exerciseId: String): ContentUrls = networkCall(
        { contentService.getExerciseContent(lessonId, exerciseId, "1.0", "Male") },
        { it.data.url.toContentUrls() }
    )

    override suspend fun getSongPracticeContentUrls(songId: String): ContentUrls = networkCall(
        { contentService.getSongPracticeContentUrls(songId, "1.0") },
        { it.data.url.toContentUrls() }
    )
}