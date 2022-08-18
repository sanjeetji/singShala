package com.sensibol.lucidmusic.singstr.gui.app.explore

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.app.feed.generic.GenericFeedSlotsViewModel
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentExploreBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
internal class ExploreFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_explore
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentExploreBinding::inflate
    override val binding: FragmentExploreBinding get() = super.binding as FragmentExploreBinding

    @Inject
    lateinit var exploreGenresAdapter: ExploreGenresAdapter

    @Inject
    internal lateinit var exploreFeedCoversAdapter: ExploreFeedCoversAdapter

    @Inject
    lateinit var exploreSongsAdapter: ExploreSongsAdapter

    @Inject
    internal lateinit var trendingSongsAdapter: TrendingSongsAdapter

    private val genericFeedSlotVM: GenericFeedSlotsViewModel by activityViewModels()
    private val viewModel: ExploreViewModel by viewModels()
    private var selectedGenresPos = 0;

    override fun onInitView() {
        Analytics.logEvent(
            Analytics.Event.SearchPageViewEvent(
                Analytics.Event.Param.ScrollPercent("NA")
            )
        )
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(genres, ::showGenres)
            observe(getSongs, ::showCategorySong)
            observe(feed, ::showFeeds)
            observe(getTrendingSongs, ::showTrendingSongs)

            loadGenres()
            if (exploreFeedCoversAdapter.itemCount == 0){
                loadFeed()
            }
        }

        binding.apply {
            pbLoading.visibility = VISIBLE
            rvGenres.apply {
                layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = exploreGenresAdapter
            }

            rvAllExploreSongs.visibility = if (selectedGenresPos == 0) VISIBLE else GONE

            rvTrendingSongs.visibility = if (selectedGenresPos == 1) VISIBLE else GONE

            svSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val searchKeyword = binding.svSearch.text.toString().trim()
                    findNavController().navigate(ExploreFragmentDirections.actionExploreFragmentToSearchFragment(searchKeyword))
                    Analytics.logEvent(
                        Analytics.Event.SearchQueryEvent(
                            Analytics.Event.Param.SearchQuery(searchKeyword),
                        )
                    )
                    return@OnEditorActionListener true
                }
                false
            })

            rvAllExploreSongs.apply {
                layoutManager = GridLayoutManager(root.context, 3, GridLayoutManager.VERTICAL, false)
//                layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)
                adapter = exploreFeedCoversAdapter
            }

            rvAllExploreSongs.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        Timber.d("Need to load more data")
                        binding.pbLoading.visibility = VISIBLE
                        viewModel.loadFeed()
                    }
                }
            })

            exploreFeedCoversAdapter.apply {
                onCoverClickListener = { _, position ->
                    val modPos = position + 1
                    val slotPos = if(modPos > 12) (modPos / 12)  else 0
                    genericFeedSlotVM.setFeed(viewModel.feeds[slotPos])
                    val pos = if (modPos > 12) (modPos % 12) - 1 else position
                    findNavController().navigate(ExploreFragmentDirections.toGenericFeedFragment(pos))
                }

                onLoadMoreListener = { cover, position ->
                    Timber.d("Need to load more feed cover")
                }
            }

            rvExploreSongs.apply {
                layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)
                adapter = exploreSongsAdapter
            }
            exploreSongsAdapter.apply {
                onSingClickListener = { songMini ->
                    findNavController().navigate(ExploreFragmentDirections.toPrepareSingHostFragment(SingMode.RECORD.argName, songMini.id))
                }

                onCoverClickListener = { _, songMini, position ->
                    findNavController().navigate(ExploreFragmentDirections.toSongFeedFragment(songMini.id, position))
                }
            }

            exploreGenresAdapter.onGenreClickListener = {
                pbLoading.visibility = VISIBLE
                when (it.name) {
                    "All" -> {
                        rvAllExploreSongs.visibility = VISIBLE
                        rvTrendingSongs.visibility = GONE
                        rvExploreSongs.visibility = GONE
                        viewModel.loadFeed()
                        selectedGenresPos = 0;
                    }
                    "Trending" -> {
                        viewModel.loadSongsFromTrending()
                        rvExploreSongs.visibility = GONE
                        rvAllExploreSongs.visibility = GONE
                        rvTrendingSongs.visibility = VISIBLE
                        selectedGenresPos = 1;
                    }
                    else -> {
                        viewModel.loadSongsFromCategory(it)
                        rvExploreSongs.visibility = VISIBLE
                        rvAllExploreSongs.visibility = GONE
                        rvTrendingSongs.visibility = GONE
                        selectedGenresPos = 2;
                    }
                }
            }

            rvTrendingSongs.apply {
                layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)
                adapter = trendingSongsAdapter
            }

            trendingSongsAdapter.apply {
                onSingClickListener = { songMini ->
                    findNavController().navigate(ExploreFragmentDirections.toPrepareSingHostFragment(SingMode.RECORD.argName, songMini.id))
                }

                onCoverClickListener = { _, songMini, position ->
                    findNavController().navigate(ExploreFragmentDirections.toSongFeedFragment(songMini.id, position))
                }
            }

            rvTrendingSongs.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        Timber.d("Need to load more trending song")
                        binding.pbLoading.visibility = VISIBLE
                        viewModel.loadSongsFromTrending()
                    }
                }
            })
        }
    }

    private fun showCategorySong(list: List<ExploreSongView>) {
        binding.pbLoading.visibility = GONE
        exploreSongsAdapter.trendingSongViews = list
        exploreSongsAdapter.notifyDataSetChanged()
    }

    private fun showGenres(genres: List<Genre>) {
        val mutableListOfGenres: MutableList<Genre> = genres.toMutableList()
        mutableListOfGenres.add(0, allGenre)
        mutableListOfGenres.add(1, trendingGenre)
        exploreGenresAdapter.genres = mutableListOfGenres
    }

    private fun showFeeds(feed: Feed) {
        binding.pbLoading.visibility = GONE
        exploreFeedCoversAdapter.addToExploreFeedAdapter(viewModel.covers)
    }

    private fun showTrendingSongs(trendingSongs: List<ExploreSongView>) {
        binding.pbLoading.visibility = GONE
        trendingSongsAdapter.addToTrendingSongsView(trendingSongs)
    }
}