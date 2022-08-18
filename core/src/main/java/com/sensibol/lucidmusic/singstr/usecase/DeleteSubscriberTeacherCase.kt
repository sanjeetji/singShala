package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.SubscribeUser
import com.sensibol.lucidmusic.singstr.domain.model.TeacherSubscribeUnsbscribe
import com.sensibol.lucidmusic.singstr.domain.webservice.FeedWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.LearnWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSLearnWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import javax.inject.Inject

class DeleteSubscriberTeacherCase @Inject constructor(
    private val learnWebService: LearnWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke( teacherId: String): Boolean {
        val authToken = appDatabase.getAuthToken()
        return learnWebService.deleteSubscriberTeacher(authToken, teacherId)
    }
}