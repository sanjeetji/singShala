package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.ConceptInfo
import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.sensibol.lucidmusic.singstr.domain.webservice.LearnWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSLearnWebService
import javax.inject.Inject

class GetLessonByConceptUseCase @Inject constructor(
    private val learnWebService: NodeJSLearnWebService,
    private val appDatabase: AppDatabase,
){
    suspend operator fun invoke(conceptId: String) : ConceptInfo{
        return learnWebService.getLessonListByConcept(appDatabase.getAuthToken(), conceptId)
    }
}