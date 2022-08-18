package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.TeacherDetails
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSLearnWebService
import javax.inject.Inject

class GetTeacherDetailUseCase @Inject constructor(
    private val nodeJSLearnWebService: NodeJSLearnWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(teacherId: String?): TeacherDetails {
        val authToken = appDatabase.getAuthToken()
        return nodeJSLearnWebService.getTeacherAllDetails(authToken,teacherId)

    }
}