package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.Comments
import com.sensibol.lucidmusic.singstr.domain.model.McqQuestion
import com.sensibol.lucidmusic.singstr.domain.webservice.FeedWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.LearnWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJsFeedWebService
import javax.inject.Inject

class AddFeedCommentReplyUseCase @Inject constructor(
    private val nodeJsFeedWebService: NodeJsFeedWebService,
    private val appDatabase: AppDatabase,
) {
    suspend operator fun invoke(attemptId: String, commentId:String, reply: String,tagUserId:List<String>): String {
        val authToken = appDatabase.getAuthToken()
        return nodeJsFeedWebService.addFeedReplyComment(authToken, attemptId,commentId, reply,tagUserId)
    }
}