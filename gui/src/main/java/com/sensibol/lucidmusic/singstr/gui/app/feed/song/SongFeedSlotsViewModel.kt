package com.sensibol.lucidmusic.singstr.gui.app.feed.song

import com.sensibol.lucidmusic.singstr.domain.model.Feed
import com.sensibol.lucidmusic.singstr.gui.app.feed.FeedSlotsViewModel
import com.sensibol.lucidmusic.singstr.usecase.GetCoverCommentsUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetCoverVideoUrlUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetFeedBySongUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetFollowingFeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class SongFeedSlotsViewModel @Inject constructor(
    private val getFeedBySongUseCase: GetFeedBySongUseCase,
    override val getCoverVideoUrlUseCase: GetCoverVideoUrlUseCase,
    override val getCoverCommentsUseCase: GetCoverCommentsUseCase,
    private val getFollowingFeedUseCase: GetFollowingFeedUseCase
) : FeedSlotsViewModel(getCoverVideoUrlUseCase, getCoverCommentsUseCase) {

    private var nextPageTokenFollowingFeed: String? = null

    private var nextPageToken: String? = null

    override suspend fun getFeed(id: String): Feed =
        getFeedBySongUseCase(id, nextPageToken).also {
            nextPageToken = it.nextPageToken
        }

    override suspend fun getFollowingFeed(): Feed =
        getFollowingFeedUseCase(nextPageTokenFollowingFeed).also {
            nextPageTokenFollowingFeed = it.nextPageToken
        }

}