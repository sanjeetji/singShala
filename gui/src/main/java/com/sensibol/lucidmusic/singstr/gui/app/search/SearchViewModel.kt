package com.sensibol.lucidmusic.singstr.gui.app.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.SearchTags
import com.sensibol.lucidmusic.singstr.usecase.GetSearchTagsUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetSearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getSearchTagsUseCase: GetSearchTagsUseCase,
    private val getSearchUseCase: GetSearchUseCase
) : BaseViewModel() {

    private val _searchTags: MutableLiveData<List<SearchTags>> by lazy { MutableLiveData<List<SearchTags>>() }
    internal val searchTags: LiveData<List<SearchTags>> = _searchTags

    internal fun loadSearchTags() {
        launchUseCases {
            _searchTags.postValue(getSearchTagsUseCase())
        }
    }

    internal lateinit var keyword: String
    internal lateinit var lookup: String

    val searchResults: Flow<PagingData<SearchNode>> = Pager(
        config = PagingConfig(
            10,
            enablePlaceholders = false,
            prefetchDistance = 10
        )
    ) {
        SearchPageSource(getSearchUseCase, keyword, lookup)
    }.flow
}