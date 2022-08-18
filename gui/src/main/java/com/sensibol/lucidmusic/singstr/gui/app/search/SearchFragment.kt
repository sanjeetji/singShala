package com.sensibol.lucidmusic.singstr.gui.app.search

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.SearchTags
import com.sensibol.lucidmusic.singstr.domain.model.SingMode
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentSearchBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
internal class SearchFragment : BaseFragment() {

    override val layoutResId = R.layout.fragment_search
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentSearchBinding::inflate
    override val binding: FragmentSearchBinding get() = super.binding as FragmentSearchBinding

    private val viewModel: SearchViewModel by viewModels()

    @Inject
    internal lateinit var searchTabAdapter: SearchTabAdapter

    internal lateinit var advanceSearchAdapter: AdvanceSearchAdapter

    private val args: SearchFragmentArgs by navArgs()

    private var searchLookUp: String = ""
    private var searchKeyword: String = ""
    internal var arr: MutableList<String> = mutableListOf()

    override fun onInitView() {

        viewModel.keyword = args.searchKeyword
        viewModel.lookup = searchLookUp

        advanceSearchAdapter = AdvanceSearchAdapter()
        binding.apply {
            rvSearch.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                setHasFixedSize(true)
                adapter = advanceSearchAdapter
            }
        }

        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(searchTags, ::showSearchTags)
            loadSearchTags()

            if (searchLookUp.isNotEmpty()) {
                loadSearchTags()
            }
            searchResultFlow()
        }

        binding.apply {
            edtSearch.setText(args.searchKeyword)
            rvSearchCategories.apply {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        val startMargin = if (0 == parent.getChildAdapterPosition(view)) resources.getDimension(R.dimen.screen_horz_margin) else 0
                        outRect.set(startMargin.toInt(), 0, 0, 0)
                    }
                })
                adapter = searchTabAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
            edtSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchKeyword = binding.edtSearch.text.toString().trim()
                    viewModel.keyword = searchKeyword
                    searchResultFlow()
                    return@OnEditorActionListener true
                }
                false
            })
            searchTabAdapter.onSearchFilterTagClickListener = { isChecked: Boolean, tagName: String ->
                if (tagName == "ALL") {
                    Analytics.logEvent(
                        Analytics.Event.SearchFilterClickEvent(
                            Analytics.Event.Param.FilterState(tagName),
                        )
                    )
                    searchLookUp = ""
                } else {
                    when (isChecked) {
                        true -> {
                            arr.add(tagName)
                            if (searchLookUp == "") {
                                searchLookUp = tagName
                            } else if (searchLookUp != "") {
                                val plus = searchLookUp.plus(",").plus(tagName)
                                searchLookUp = plus
                            }
                            Analytics.logEvent(
                                Analytics.Event.SearchFilterClickEvent(
                                    Analytics.Event.Param.FilterState(arr.toString()),
                                )
                            )
                        }
                        false -> {
                            if (arr.isNotEmpty()) {
                                arr.removeAt(arr.indexOf(tagName))
                                val joinToString = arr.joinToString(",")
                                searchLookUp = joinToString
                                Analytics.logEvent(
                                    Analytics.Event.SearchFilterClickEvent(
                                        Analytics.Event.Param.FilterState(arr.toString()),
                                    )
                                )
                            }
                        }
                    }
                }
                viewModel.lookup = searchLookUp
                searchResultFlow()
            }

            advanceSearchAdapter.onLessonClickListener = { lesson ->
                findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToLessonFragment(lesson.id))
            }

            advanceSearchAdapter.onSongClickListener = { song ->
                findNavController().navigate(
                    SearchFragmentDirections.toPrepareSingHostFragment(SingMode.RECORD.name, song.id)
                )
            }

            advanceSearchAdapter.onUserClickListener = { user ->
                findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToOtherUserProfileFragment(user.id))
            }

            advanceSearchAdapter.addLoadStateListener { loadState ->
                if (loadState.source.refresh is LoadState.NotLoading
                    && loadState.append.endOfPaginationReached
                    && advanceSearchAdapter.itemCount < 1
                ) {
                    rvSearch.isVisible = false
                    tvNoSearchResult.isVisible = true
                } else {
                    rvSearch.isVisible = true
                    tvNoSearchResult.isVisible = false
                }
            }
        }
    }

    private fun showSearchTags(searchTags: List<SearchTags>) {
        searchTabAdapter.searchTags = searchTags
    }

    private fun searchResultFlow() {
        lifecycleScope.launch {
            viewModel.searchResults.collectLatest {
                advanceSearchAdapter.submitData(it)
            }
        }
    }
}