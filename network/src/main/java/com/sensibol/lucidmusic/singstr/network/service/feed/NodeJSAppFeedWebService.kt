package com.sensibol.lucidmusic.singstr.network.service.feed

import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJsFeedWebService
import com.sensibol.lucidmusic.singstr.network.service.networkCall
import com.sensibol.lucidmusic.singstr.network.service.toBearerToken
import javax.inject.Inject

internal class NodeJSAppFeedWebService @Inject constructor(
    private val nodeJSRetrofitFeedWebService: NodeJSRetrofitFeedWebService
) : NodeJsFeedWebService {

    override suspend fun getFollowingFeed(authToken: AuthToken,pageToken: String?): Feed = networkCall(
        { nodeJSRetrofitFeedWebService.getFeedFollowing(authToken.toBearerToken(),pageToken)},
        { response -> response.data.toFeed() }
    )

    override suspend fun addFeedReplyComment(authToken: AuthToken, attemptId: String, commentId: String, reply: String,tagUserId:List<String>): String = networkCall(
        { nodeJSRetrofitFeedWebService.addFeedReplyComment(authToken.toBearerToken(), ReplyOnCommentRequest(attemptId,commentId,reply,tagUserId)) },
        { response -> response.data }
    )

    override suspend fun getFeedCommentReply(authToken: AuthToken, commentId: String): AllReplyComment = networkCall(
        {nodeJSRetrofitFeedWebService.getFeedCommentReply(authToken.toBearerToken(),commentId)},
        {response -> response.data.get(0).toAllReplyComment() }
    )

    override suspend fun addFeedComment(authToken: AuthToken, comment: String, attemptId: String, tagUserId: List<String>): String = networkCall(
        {nodeJSRetrofitFeedWebService.addFeedCommentOnCover(authToken.toBearerToken(),CommentOnCoverRequest(comment,attemptId,tagUserId))},
        {response -> response.data }
    )

}