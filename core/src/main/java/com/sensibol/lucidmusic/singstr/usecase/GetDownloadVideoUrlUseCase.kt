package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.DownloadVideoUrl
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSUserWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import javax.inject.Inject

class GetDownloadVideoUrlUseCase @Inject constructor(
    private val userWebService: NodeJSUserWebService
) {

    suspend operator fun invoke(attemptId: String): DownloadVideoUrl{
        return userWebService.getDownloadVideoUrl(attemptId)
    }
}