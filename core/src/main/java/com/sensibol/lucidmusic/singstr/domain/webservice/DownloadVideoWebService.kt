package com.sensibol.lucidmusic.singstr.domain.webservice

import com.sensibol.lucidmusic.singstr.domain.model.DownloadVideoUrl

interface DownloadVideoWebService {

    suspend fun getDownloadVideo(videoUrl: String, filePath: String, attemptId: String)
}