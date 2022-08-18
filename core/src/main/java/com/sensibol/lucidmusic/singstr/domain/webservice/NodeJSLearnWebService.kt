package com.sensibol.lucidmusic.singstr.domain.webservice

import com.sensibol.lucidmusic.singstr.domain.model.*

interface NodeJSLearnWebService {

    suspend fun getExerciseFromId(authToken: AuthToken, exerciseId: String): ExerciseFromId

    suspend fun getLessonListByConcept(authToken: AuthToken, conceptId: String): ConceptInfo

    suspend fun addAllToMyList(authToken: AuthToken, lessonIds: List<String>): String

    suspend fun getTeacherAllDetails(authToken: AuthToken,teacherId:String?):TeacherDetails

}