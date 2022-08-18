package com.sensibol.lucidmusic.singstr.gui.app.feed.user

import com.sensibol.lucidmusic.singstr.domain.model.Feed
import com.sensibol.lucidmusic.singstr.gui.app.feed.FeedSlotsViewModel
import com.sensibol.lucidmusic.singstr.usecase.GetCoverCommentsUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetCoverVideoUrlUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetFeedByUserUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetFollowingFeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class UserFeedSlotsViewModel @Inject constructor(
    private val getFeedByUserUseCase: GetFeedByUserUseCase,
    override val getCoverVideoUrlUseCase: GetCoverVideoUrlUseCase,
    override val getCoverCommentsUseCase: GetCoverCommentsUseCase,
    private val getFollowingFeedUseCase: GetFollowingFeedUseCase
) : FeedSlotsViewModel(getCoverVideoUrlUseCase, getCoverCommentsUseCase) {

    private var nextPageTokenFollowingFeed: String? = null

    private var nextPageToken: String? = null

    override suspend fun getFeed(id: String): Feed =
        getFeedByUserUseCase(id, nextPageToken).also {
            nextPageToken = it.nextPageToken
        }

    override suspend fun getFollowingFeed(): Feed =
        getFollowingFeedUseCase(nextPageTokenFollowingFeed).also {
            nextPageTokenFollowingFeed = it.nextPageToken
        }
}