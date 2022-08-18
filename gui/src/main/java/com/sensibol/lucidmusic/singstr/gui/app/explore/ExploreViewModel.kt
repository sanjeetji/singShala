package com.sensibol.lucidmusic.singstr.gui.app.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.Cover
import com.sensibol.lucidmusic.singstr.domain.model.Feed
import com.sensibol.lucidmusic.singstr.domain.model.Genre
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class ExploreViewModel @Inject constructor(
    private val getGenresUseCase: GetGenresUseCase,
    private val getFeedUseCase: GetFeedUseCase,
    private val getSongFromCategoryUseCase: GetTrendingSongsUseCase,
    private val getFeedBySongUseCase: GetFeedBySongUseCase,
    private val getTrendingSongsPagingUseCase: GetTrendingSongsPagingUseCase
) : BaseViewModel() {

    private val _genres: MutableLiveData<List<Genre>> by lazy { MutableLiveData<List<Genre>>() }
    internal val genres: LiveData<List<Genre>> = _genres

    internal fun loadGenres() {
        launchUseCases {
            _genres.postValue(getGenresUseCase())
        }
    }

    private val _getSongs: MutableLiveData<List<ExploreSongView>> by lazy { MutableLiveData<List<ExploreSongView>>() }
    internal val getSongs: LiveData<List<ExploreSongView>> = _getSongs

    internal fun loadSongsFromCategory(genre: Genre? = null) {
        Timber.v("loadTrendingSongs: IN")
        launchUseCases {
            val songs = getSongFromCategoryUseCase(genre)
            val result: MutableList<ExploreSongView> = mutableListOf()
            songs.forEach {
                val feed = getFeedBySongUseCase(it.id)
                if (feed.covers.isNotEmpty()) {
                    result.add(ExploreSongView(it, feed.covers))
                }
            }
            _getSongs.postValue(result)
        }
    }

    var pagePosition = 0;
    public val feeds = mutableListOf<Feed>()
    public val covers = mutableListOf<Cover>()
    private var pageToken: String ? = null
    private val _feed: MutableLiveData<Feed> by lazy { MutableLiveData<Feed>() }
    internal val feed: LiveData<Feed> = _feed

    internal fun loadFeed() {
        Timber.d("load feed")
        launchUseCases {
            val feed = getFeedUseCase(pageToken)
            _feed.postValue(feed)
            feeds.add(feed)
            covers.addAll(feed.covers)
            pageToken = feed.nextPageToken
            pagePosition += 1
//            _feed.postValue(getFeedUseCase(pageToken))
        }
    }

    val trendingPagingResult: Flow<PagingData<SongMini>> = Pager(
        config = PagingConfig(
            10,
            enablePlaceholders = false,
            prefetchDistance = 10
        )
    ) {
        TrendingPageSource(getTrendingSongsPagingUseCase)
    }.flow

    private var trendingPageNo: Int = 0
    private val _getTrendingSongs: MutableLiveData<List<ExploreSongView>> by lazy { MutableLiveData<List<ExploreSongView>>() }
    internal val getTrendingSongs: LiveData<List<ExploreSongView>> = _getTrendingSongs

//    internal fun loadPagingResult(){
//        launchUseCases {
//            val songs  = getTrendingSongsPagingUseCase(0)
//            val result: MutableList<ExploreSongView> = mutableListOf()
//            songs.forEach {
//                val feed = getFeedBySongUseCase(it.id)
//                if (feed.covers.isNotEmpty()) {
//                    result.add(ExploreSongView(it, feed.covers))
//                }
//            }
//            _getTrendingSongs.postValue(result)
//
//        }
//    }

//    internal fun loadTrendingSongs(songs: List<SongMini>){
//        val result: MutableList<ExploreSongView> = mutableListOf()
//        launchUseCases {
//            songs.forEach {
//                val feed = getFeedBySongUseCase(it.id)
//                if (feed.covers.isNotEmpty()) {
//                    result.add(ExploreSongView(it, feed.covers))
//                }
//            }
//        }
//    }
    internal fun loadSongsFromTrending() {
        Timber.v("loadTrendingSongs: IN")
        launchUseCases {
            val songs = getTrendingSongsPagingUseCase(trendingPageNo)
            val result: MutableList<ExploreSongView> = mutableListOf()
            trendingPageNo += 1
            songs.forEach {
                val feed = getFeedBySongUseCase(it.id)
                if (feed.covers.isNotEmpty()) {
                    result.add(ExploreSongView(it, feed.covers))
                }
            }
            _getTrendingSongs.postValue(result)
        }
    }
}