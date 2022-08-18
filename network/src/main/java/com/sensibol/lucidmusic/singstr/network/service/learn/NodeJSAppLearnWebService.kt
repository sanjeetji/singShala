package com.sensibol.lucidmusic.singstr.network.service.learn

import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSLearnWebService
import com.sensibol.lucidmusic.singstr.network.data.response.toLesson
import com.sensibol.lucidmusic.singstr.network.data.response.toLessonMini
import com.sensibol.lucidmusic.singstr.network.service.networkCall
import com.sensibol.lucidmusic.singstr.network.service.toBearerToken
import com.sensibol.lucidmusic.singstr.network.service.user.AddDeleteSubscriberRequestBody
import javax.inject.Inject

internal class NodeJSAppLearnWebService @Inject constructor(
    private val nodeJSLearnWebService : NodeJSRetrofitLeanWebService
) : NodeJSLearnWebService{

    override suspend fun getExerciseFromId(authToken: AuthToken, exerciseId: String): ExerciseFromId = networkCall(
        { nodeJSLearnWebService.getExerciseFromId(authToken.toBearerToken(), exerciseId) },
        { response -> response.data[0].toExerciseFromId()}
    )

    override suspend fun getLessonListByConcept(authToken: AuthToken, conceptId: String) : ConceptInfo = networkCall(
        {nodeJSLearnWebService.getLessonListByConceptId(authToken.toBearerToken(), conceptId)},
        {response -> response.data[0].toConceptInfo() }
    )

    override suspend fun addAllToMyList(authToken: AuthToken, lessonIds: List<String>) : String = networkCall(
        {nodeJSLearnWebService.addAllToMyList(authToken.toBearerToken(), SaveAllToMyListRequestBody(lessonIds))},
        {response -> response.data }
    )

    override suspend fun getTeacherAllDetails(authToken: AuthToken, teacherId: String?):TeacherDetails = networkCall(
        {nodeJSLearnWebService.getTeacherAllDetails(authToken.toBearerToken(),teacherId)},
        {response -> response.data.map { it.toTeacherDetails() }[0]}
    )
}