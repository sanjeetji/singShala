package com.sensibol.lucidmusic.singstr.gui.app.explore

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.facebook.internal.Mutable
import com.sensibol.lucidmusic.singstr.domain.model.SearchData
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.gui.app.search.SearchNode
import com.sensibol.lucidmusic.singstr.gui.app.search.SearchNodeLesson
import com.sensibol.lucidmusic.singstr.gui.app.search.SearchNodeSong
import com.sensibol.lucidmusic.singstr.gui.app.search.SearchNodeUser
import com.sensibol.lucidmusic.singstr.usecase.GetTrendingSongsPagingUseCase
import java.io.IOException

class TrendingPageSource(
    val getTrendingSongsPagingUseCase: GetTrendingSongsPagingUseCase
) : PagingSource<Int, SongMini>(){

    private val INDEX_PAGE = 0

    override fun getRefreshKey(state: PagingState<Int, SongMini>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SongMini> {
        val page = params.key ?: INDEX_PAGE
        return try {
            val response = getTrendingSongsPagingUseCase.invoke(page)
            val nextKey = if (response.isEmpty()) {
                null
            } else {
                page + 1
            }
            LoadResult.Page(
                data = response,
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