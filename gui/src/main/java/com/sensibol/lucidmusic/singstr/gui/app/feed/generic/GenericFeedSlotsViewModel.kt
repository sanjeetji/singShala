package com.sensibol.lucidmusic.singstr.gui.app.feed.generic

import com.sensibol.lucidmusic.singstr.domain.model.Feed
import com.sensibol.lucidmusic.singstr.gui.app.feed.FeedSlotsViewModel
import com.sensibol.lucidmusic.singstr.usecase.GetCoverCommentsUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetCoverVideoUrlUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetFeedUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetFollowingFeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class GenericFeedSlotsViewModel @Inject constructor(
    private val getFeedUseCase: GetFeedUseCase,
    override val getCoverVideoUrlUseCase: GetCoverVideoUrlUseCase,
    override val getCoverCommentsUseCase: GetCoverCommentsUseCase,
    private val getFollowingFeedUseCase: GetFollowingFeedUseCase
) : FeedSlotsViewModel(getCoverVideoUrlUseCase, getCoverCommentsUseCase) {

    private var nextPageToken: String? = null

    private var nextPageTokenFollowingFeed: String? = null

    override fun reset() {
        nextPageToken = null
        nextPageTokenFollowingFeed = null
        super.reset()
    }

    override fun setFeed(feed: Feed) {
        nextPageToken = feed.nextPageToken
        super.setFeed(feed)
    }

    override suspend fun getFeed(id: String): Feed =
        getFeedUseCase(nextPageToken).also {
            nextPageToken = it.nextPageToken
        }

    override suspend fun getFollowingFeed(): Feed =
        getFollowingFeedUseCase(nextPageTokenFollowingFeed).also {
            nextPageTokenFollowingFeed = it.nextPageToken
        }

}