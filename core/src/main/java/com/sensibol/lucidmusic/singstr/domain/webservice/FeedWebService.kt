package com.sensibol.lucidmusic.singstr.domain.webservice

import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import com.sensibol.lucidmusic.singstr.domain.model.Comment
import com.sensibol.lucidmusic.singstr.domain.model.DraftsPage
import com.sensibol.lucidmusic.singstr.domain.model.Feed

interface FeedWebService {

    suspend fun getFeed(pageToken: String?): Feed

    suspend fun getCoverVideoUrl(attemptId: String): String

    suspend fun addClapCount(authToken: AuthToken, attemptId: String, count: Int)

    suspend fun sendCoverWatchedDurationMS(authToken: AuthToken, attemptId: String, watchedDurationMS: Long): Unit

    suspend fun getFeedBySongId(authToken: AuthToken, songId: String, pageToken: String?): Feed

    suspend fun getFeedByCoverId(authToken: AuthToken, coverId: String): Feed

    suspend fun getCoverComments(authToken: AuthToken, coverId: String): List<Comment>

    suspend fun addFeedComment(authToken: AuthToken, attemptId: String, comment: String): Boolean

    suspend fun deleteCover(authToken: AuthToken, attemptId: String): Boolean

    suspend fun getFeedByUser(authToken: AuthToken?, userId: String, pageToken: String?): Feed

    suspend fun getDraftCovers(authToken: AuthToken, pageToken: String?): DraftsPage
}