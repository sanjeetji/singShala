package com.sensibol.lucidmusic.singstr.gui.app.feed.cover

import com.sensibol.lucidmusic.singstr.domain.model.Feed
import com.sensibol.lucidmusic.singstr.gui.app.feed.FeedSlotsViewModel
import com.sensibol.lucidmusic.singstr.usecase.GetCoverCommentsUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetCoverVideoUrlUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetFeedByCoverUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetFollowingFeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class CoverFeedSlotsViewModel @Inject constructor(
    private val getFeedByCoverUseCase: GetFeedByCoverUseCase,
    override val getCoverVideoUrlUseCase: GetCoverVideoUrlUseCase,
    override val getCoverCommentsUseCase: GetCoverCommentsUseCase,
    private val getFollowingFeedUseCase: GetFollowingFeedUseCase
) : FeedSlotsViewModel(getCoverVideoUrlUseCase, getCoverCommentsUseCase) {

    private var nextPageTokenFollowingFeed: String? = null

    override suspend fun getFeed(id: String): Feed = getFeedByCoverUseCase(id)

    override suspend fun getFollowingFeed(): Feed =
        getFollowingFeedUseCase(nextPageTokenFollowingFeed).also {
            nextPageTokenFollowingFeed = it.nextPageToken
        }
}