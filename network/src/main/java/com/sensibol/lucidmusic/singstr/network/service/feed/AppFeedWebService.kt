package com.sensibol.lucidmusic.singstr.network.service.feed

import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import com.sensibol.lucidmusic.singstr.domain.model.Comment
import com.sensibol.lucidmusic.singstr.domain.model.DraftsPage
import com.sensibol.lucidmusic.singstr.domain.model.Feed
import com.sensibol.lucidmusic.singstr.domain.webservice.FeedWebService
import com.sensibol.lucidmusic.singstr.network.service.networkCall
import com.sensibol.lucidmusic.singstr.network.service.networkRequest
import com.sensibol.lucidmusic.singstr.network.service.toBearerToken
import javax.inject.Inject

internal class AppFeedWebService @Inject constructor(
    private val feedService: RetrofitFeedWebService
) : FeedWebService {

    override suspend fun getFeed(pageToken: String?): Feed = networkCall(
        { feedService.getFeed(pageToken) },
        { response -> response.data.toFeed() }
    )

    override suspend fun getCoverVideoUrl(attemptId: String): String = networkCall(
        { feedService.getCoverVideoUrl(attemptId) },
        { response -> response.data.url }
    )

    override suspend fun addClapCount(authToken: AuthToken, attemptId: String, count: Int): Unit = networkRequest {
        feedService.addClap(authToken.toBearerToken(), ClapCountRequest(attemptId, count))
    }

    override suspend fun sendCoverWatchedDurationMS(authToken: AuthToken, attemptId: String, watchedDurationMS: Long): Unit = networkRequest {
        feedService.sendCoverWatchedDurationMS(authToken.toBearerToken(), CoverWatchedRequest(attemptId, watchedDurationMS))
    }

    override suspend fun getFeedBySongId(authToken: AuthToken, songId: String, pageToken: String?): Feed = networkCall(
        { feedService.getFeedBySong(authToken.toBearerToken(), songId, pageToken) },
        { response -> response.data.toFeed() }
    )

    override suspend fun getFeedByCoverId(authToken: AuthToken, coverId: String): Feed = networkCall(
        { feedService.getFeedByCover(authToken.toBearerToken(), coverId) },
        { response -> response.data.toFeed() }
    )

    override suspend fun getCoverComments(authToken: AuthToken, coverId: String): List<Comment> = networkCall(
        { feedService.getCoverComments(authToken.toBearerToken(), coverId) },
        { response -> response.toComments() }
    )

    override suspend fun addFeedComment(authToken: AuthToken, attemptId: String, comment: String): Boolean = networkCall(
        { feedService.addFeedComment(authToken.toBearerToken(), AddFeedCommentRequest(comment, attemptId)) },
        { response -> response.success }
    )

    override suspend fun deleteCover(authToken: AuthToken, attemptId: String): Boolean = networkCall(
        { feedService.deleteCover(authToken.toBearerToken(), DeleteCoverRequestBody(attemptId)) },
        { response -> response.success }
    )

    override suspend fun getFeedByUser(authToken: AuthToken?, userId: String, pageToken: String?): Feed = networkCall(
        { feedService.getFeedByUser(authToken?.toBearerToken(), userId, pageToken) },
        { response -> response.data.toFeed() }
    )

    override suspend fun getDraftCovers(authToken: AuthToken, pageToken: String?): DraftsPage = networkCall(
        { feedService.getDraftCovers(authToken?.toBearerToken(), pageToken) },
        { response -> response.data.toDrafts() }
    )

}