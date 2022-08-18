package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSUserWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import timber.log.Timber
import javax.inject.Inject

class DownloadVideoContentUseCase @Inject constructor(
    private val downloadVideoUseCase: DownloadVideoUseCase,
    private val userWebService: NodeJSUserWebService
) {
    suspend operator fun invoke(attemptId: String): String {

        val videoUrl =  userWebService.getDownloadVideoUrl(attemptId).videoUrl
        return  downloadVideoUseCase.invoke(videoUrl).videoFile.absolutePath

    }
}