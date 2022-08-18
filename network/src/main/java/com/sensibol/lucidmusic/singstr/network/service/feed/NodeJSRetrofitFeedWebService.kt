package com.sensibol.lucidmusic.singstr.network.service.feed

import com.sensibol.lucidmusic.singstr.network.service.AUTH_HEADER
import retrofit2.http.*

internal interface NodeJSRetrofitFeedWebService {

    @GET("watch/feed/following")
    suspend fun getFeedFollowing(
        @Header(AUTH_HEADER) token: String,
        @Query("page_token", encoded = true) pageToken: String?,
    ): GetFollowingFeedResponse

    @POST("watch/comment/reply")
    suspend fun addFeedReplyComment(
        @Header(AUTH_HEADER) token: String,
        @Body replyOnCommentRequest: ReplyOnCommentRequest
    ):AddFeedCommentReplyResponse

    @GET("watch/comment/{commentId}")
    suspend fun getFeedCommentReply(
        @Header(AUTH_HEADER) token: String,
        @Path("commentId") commentId: String
    ):GetFeedCommentReplyResponse

    @POST("watch/add/comment")
    suspend fun addFeedCommentOnCover(
        @Header(AUTH_HEADER) token: String,
        @Body commentOnCoverRequest: CommentOnCoverRequest
    ):AddFeedCommentOnCoverResponse

}