package com.sensibol.lucidmusic.singstr.domain.webservice

import com.sensibol.lucidmusic.singstr.domain.model.ContentUrls

interface ContentWebService {

    suspend fun getSongContentUrls(songId: String): ContentUrls

    @Deprecated("lessonId is being passed to support incorrect backend")
    suspend fun getExerciseContent(lessonId: String, exerciseId: String): ContentUrls

    suspend fun getSongPracticeContentUrls(songId: String): ContentUrls

}