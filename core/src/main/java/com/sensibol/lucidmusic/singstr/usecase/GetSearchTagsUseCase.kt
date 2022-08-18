package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.model.SearchTags
import javax.inject.Inject


class GetSearchTagsUseCase @Inject constructor() {
    operator fun invoke(): List<SearchTags> {
        val searchTags = mutableListOf<SearchTags>()
        val all = SearchTags("0", "ALL", true)
        val song = SearchTags("1", "song", false)
        val user = SearchTags("2", "user", false)
        val lesson = SearchTags("3", "lesson", false)
        searchTags.add(all)
        searchTags.add(song)
        searchTags.add(user)
        searchTags.add(lesson)
        return searchTags
    }
}