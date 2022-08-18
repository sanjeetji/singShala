package com.sensibol.lucidmusic.singstr.gui.app.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.sensibol.lucidmusic.singstr.domain.model.SearchData
import com.sensibol.lucidmusic.singstr.gui.databinding.TileSearchDummyBinding
import com.sensibol.lucidmusic.singstr.gui.databinding.TileSearchLessonBinding
import com.sensibol.lucidmusic.singstr.gui.databinding.TileSearchSongBinding
import com.sensibol.lucidmusic.singstr.gui.databinding.TileSearchUserBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import timber.log.Timber

class AdvanceSearchAdapter : PagingDataAdapter<SearchNode, AdvanceSearchAdapter.BaseViewHolder>(SearchViewDiff()) {

    var onLessonClickListener: (SearchData.Lesson) -> Unit = {}
    var onUserClickListener: (SearchData.User) -> Unit = {}
    var onSongClickListener: (SearchData.Song) -> Unit = {}

    companion object {

        const val VIEW_TYPE_IGNORE = 0
        const val VIEW_TYPE_USER = 1
        const val VIEW_TYPE_LESSON = 2
        const val VIEW_TYPE_SONG = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SearchNodeLesson -> VIEW_TYPE_LESSON
            is SearchNodeUser -> VIEW_TYPE_USER
            is SearchNodeSong -> VIEW_TYPE_SONG
            else -> VIEW_TYPE_IGNORE
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_SONG -> SearchSongVH(parent)
            VIEW_TYPE_USER -> SearchUserVH(parent)
            VIEW_TYPE_LESSON -> SearchLessonVH(parent)
            else -> DefaultVH(parent)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        Timber.d("onBindViewHolder: $position, ${holder.displayName}")
        val searchResults = getItem(position)
        if (searchResults != null) {
            holder.bind(searchResults)
        }
    }

    abstract class BaseViewHolder(protected val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        abstract fun bind(search: SearchNode)
    }

    private inner class DefaultVH(parent: ViewGroup) :
        BaseViewHolder(TileSearchDummyBinding.inflate(LayoutInflater.from(parent.context), parent, false)) {
        override fun bind(search: SearchNode) {
        }
    }

    private inner class SearchUserVH(parent: ViewGroup) :
        BaseViewHolder(TileSearchUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)) {

        override fun bind(search: SearchNode) {
            val binding = binding as TileSearchUserBinding
            binding.apply {
                search.user?.let { user ->
                    tvUserName.text = user.firstName
                    tvUserHandle.text = user.userHandle
                    ivProfilePic.loadUrl(user.profileImg)
                }

                binding.root.setOnClickListener {
                    search.user?.let { user ->
                        onUserClickListener(user)
                    }
                }
            }
        }
    }

    private inner class SearchLessonVH(parent: ViewGroup) :
        BaseViewHolder(TileSearchLessonBinding.inflate(LayoutInflater.from(parent.context), parent, false)) {

        override fun bind(search: SearchNode) {
            val binding = binding as TileSearchLessonBinding
            binding.apply {
                search.lesson?.let { lesson ->
                    tvLessonTitle.text = lesson.title
                    tvPrimaryTag.text = lesson.primaryTag
                    tvLessonDifficulty.text = lesson.difficulty
                    ivThumbnail.loadUrl(lesson.thumbnailUrl)
                }
                binding.root.setOnClickListener {
                    search.lesson?.let { lesson ->
                        onLessonClickListener(lesson)
                    }
                }
            }
        }
    }

    private inner class SearchSongVH(parent: ViewGroup) :
        BaseViewHolder(TileSearchSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)) {

        override fun bind(search: SearchNode) {
            val binding = binding as TileSearchSongBinding
            binding.apply {
                search.song?.let { song ->
                    tvTitle.text = song.title
                    tvSubtitle.text = song.album
                    ivThumbnail.loadUrl(song.thumbnailUrl)
                }
                tvSing.setOnClickListener {
                    search.song?.let { song ->
                        onSongClickListener(song)
                    }
                }
            }
        }
    }

    class SearchViewDiff : DiffUtil.ItemCallback<SearchNode>() {
        override fun areItemsTheSame(oldItem: SearchNode, newItem: SearchNode): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: SearchNode, newItem: SearchNode): Boolean {
            return oldItem == newItem
        }
    }


    val Any.displayName: String
        get() = "${javaClass.simpleName}[${hashCode().toString(16)}]"
}
