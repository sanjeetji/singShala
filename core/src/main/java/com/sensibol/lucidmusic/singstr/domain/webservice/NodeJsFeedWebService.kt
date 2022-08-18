package com.sensibol.lucidmusic.singstr.domain.webservice

import com.sensibol.lucidmusic.singstr.domain.model.*

interface NodeJsFeedWebService {

    suspend fun getFollowingFeed(authToken: AuthToken, pageToken: String?): Feed

    suspend fun addFeedReplyComment(authToken: AuthToken,attemptId: String, commentId:String,reply:String,tagUserId:List<String>):String

    suspend fun getFeedCommentReply(authToken: AuthToken,commentId:String):AllReplyComment

    suspend fun addFeedComment(authToken: AuthToken, comment:String, attemptId: String,tagUserId:List<String>):String

}