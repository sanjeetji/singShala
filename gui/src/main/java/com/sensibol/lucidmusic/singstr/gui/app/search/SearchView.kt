package com.sensibol.lucidmusic.singstr.gui.app.search

import com.sensibol.lucidmusic.singstr.domain.model.SearchData

abstract class SearchNode(
    open val user: SearchData.User?,
    open val lesson: SearchData.Lesson?,
    open val song: SearchData.Song?
) {
    override fun equals(other: Any?): Boolean {
        return other is SearchNode &&
                user == other.user &&
                lesson == other.lesson &&
                song == other.song
    }
}

enum class SearchViewType {
    SearchUser,
    SearchLesson,
    SearchSong
}

data class SearchNodeUser(override val user: SearchData.User) :
    SearchNode(user, null, null)


data class SearchNodeLesson(override val lesson: SearchData.Lesson) :
    SearchNode(null, lesson, null)


data class SearchNodeSong(override val song: SearchData.Song) :
    SearchNode(null, null, song)
