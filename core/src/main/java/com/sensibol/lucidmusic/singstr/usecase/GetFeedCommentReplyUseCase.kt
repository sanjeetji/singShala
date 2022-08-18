package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.AllReplyComment
import com.sensibol.lucidmusic.singstr.domain.model.Comments
import com.sensibol.lucidmusic.singstr.domain.model.McqQuestion
import com.sensibol.lucidmusic.singstr.domain.webservice.FeedWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.LearnWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJsFeedWebService
import javax.inject.Inject

class GetFeedCommentReplyUseCase @Inject constructor(
    private val nodeJsFeedWebService: NodeJsFeedWebService,
    private val appDatabase: AppDatabase,
) {
    suspend operator fun invoke(commentId:String): AllReplyComment {
        val authToken = appDatabase.getAuthToken()
        return nodeJsFeedWebService.getFeedCommentReply(authToken,commentId)
    }
}