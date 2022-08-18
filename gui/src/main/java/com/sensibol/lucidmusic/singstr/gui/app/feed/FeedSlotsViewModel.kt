package com.sensibol.lucidmusic.singstr.gui.app.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.Cover
import com.sensibol.lucidmusic.singstr.domain.model.Feed
import com.sensibol.lucidmusic.singstr.domain.model.FollowingFeed
import com.sensibol.lucidmusic.singstr.usecase.GetCoverCommentsUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetCoverVideoUrlUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetFollowingFeedUseCase
import timber.log.Timber

internal abstract class FeedSlotsViewModel(
    protected open val getCoverVideoUrlUseCase: GetCoverVideoUrlUseCase,
    protected open val getCoverCommentsUseCase: GetCoverCommentsUseCase,
) : BaseViewModel() {

    internal var isSwitchedBtwFeedCategory = false
    internal var selectedFeedCategory = -1

    private val _feedSlots: MutableLiveData<List<FeedSlot>> by lazy { MutableLiveData() }
    internal val feedSlots: LiveData<List<FeedSlot>> = _feedSlots

    internal open fun reset() {
        _feedSlots.value = listOf()
    }

    internal open fun setFeed(feed: Feed) {
        reset()
        launchUseCases {
            _feedSlots.postValue(coversToCoverSlots(feed.covers))
        }
    }

    internal fun loadFeed(typeId: String, minCoverSlotsCount: Int = 1) {
        launchUseCases {
            var coverSlotsCount = 0
            val result: MutableList<FeedSlot> = mutableListOf()

            do {
                val feed = getFeed(typeId)
                coverSlotsCount += feed.covers.size

                result.addAll(coversToCoverSlots(feed.covers))
                feed.learnSlot.getOrNull(0)?.let {
                    result.add(LearnSlot(it.videoUrl, it))
                }
            } while (coverSlotsCount < minCoverSlotsCount)

            _feedSlots.postValue(result)
        }
    }

    abstract suspend fun getFeed(id: String): Feed

    abstract suspend fun getFollowingFeed(): Feed

    protected suspend fun coversToCoverSlots(covers: List<Cover>): List<CoverSlot> {
        return covers.map {
            CoverSlot(
                videoUrl = getCoverVideoUrlUseCase(it.id),
                cover = it,
                comments = getCoverCommentsUseCase(it.id)
            )
        }
    }

    private val _followingFeedSlots: MutableLiveData<List<FeedSlot>> by lazy { MutableLiveData() }
    internal val followingFeedSlots: LiveData<List<FeedSlot>> = _followingFeedSlots

    internal fun loadFollowingFeed() {
        launchUseCases {
            val feed = getFollowingFeed()
            _feedSlots.postValue(coversToCoverSlots(feed.covers))
        }
    }
}