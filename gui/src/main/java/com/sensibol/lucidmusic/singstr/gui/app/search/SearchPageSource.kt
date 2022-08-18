package com.sensibol.lucidmusic.singstr.gui.app.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.sensibol.lucidmusic.singstr.domain.model.SearchData
import com.sensibol.lucidmusic.singstr.usecase.GetSearchUseCase
import java.io.IOException

class SearchPageSource(
    val getSearchUseCase: GetSearchUseCase,
    val keyword: String,
    val lookup: String
) : PagingSource<Int, SearchNode>() {

    private val INDEX_PAGE = 1

    override fun getRefreshKey(state: PagingState<Int, SearchNode>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchNode> {
        val page = params.key ?: INDEX_PAGE
        return try {
            val response = getSearchUseCase.invoke(keyword, lookup, page.toString())
            val nextKey = if (response.song.isEmpty() && response.lesson.isEmpty() && response.user.isEmpty()) {
                null
            } else {
                page + 1
            }
            LoadResult.Page(
                searchResults(response),
                prevKey = if (page == INDEX_PAGE) null else page - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    private fun searchResults(searchData: SearchData): MutableList<SearchNode> {
        val searchResultsNew = mutableListOf<SearchData>()
        searchResultsNew.add(searchData)
        val searchNode = mutableListOf<SearchNode>()
        for (searchResult in searchResultsNew) {
            if (searchResult.song.isNotEmpty()) {
                for (element in searchResult.song) {
                    searchNode.add(SearchNodeSong(element))
                }
            }
            if (searchResult.user.isNotEmpty()) {
                for (element in searchResult.user) {
                    searchNode.add(SearchNodeUser(element))
                }
            }
            if (searchResult.lesson.isNotEmpty()) {
                for (element in searchResult.lesson) {
                    searchNode.add(SearchNodeLesson(element))
                }
            }
        }
        return searchNode
    }
}