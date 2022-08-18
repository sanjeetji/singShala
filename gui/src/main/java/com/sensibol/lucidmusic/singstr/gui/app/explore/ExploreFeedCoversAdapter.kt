package com.sensibol.lucidmusic.singstr.gui.app.explore

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.Cover
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.gui.databinding.TileExploreFeedAllSongCoverBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import dagger.hilt.android.scopes.FragmentScoped
import timber.log.Timber
import javax.inject.Inject
import kotlin.properties.Delegates

@FragmentScoped
internal class ExploreFeedCoversAdapter @Inject constructor() : RecyclerView.Adapter<ExploreFeedCoversAdapter.FeedCoverVH>() {

    internal var onCoverClickListener: (cover: Cover, position: Int) -> Unit = { _, _ -> }

    internal var covers: List<Cover> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    internal var onLoadMoreListener: (cover: Cover, position: Int) -> Unit = { _, _ -> }

    override fun getItemCount(): Int = covers.size

    override fun onBindViewHolder(holder: FeedCoverVH, position: Int) {
        if(position == covers.size)
            onLoadMoreListener(covers[position], position)
        holder.bind(covers[position])
    }

    fun addToExploreFeed(coverList: List<Cover>){
        covers = if(covers.isEmpty()){
            coverList
        } else{
            val result: MutableList<Cover> = mutableListOf()
            result.addAll(covers)
            result.addAll(coverList)
            Timber.d("list Size: ${result.size}")
            result
        }
        notifyDataSetChanged()
    }

    fun addToExploreFeedAdapter(coverList: List<Cover>){
        covers = coverList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedCoverVH =
        FeedCoverVH(TileExploreFeedAllSongCoverBinding.inflate(parent.layoutInflater, parent, false))

    inner class FeedCoverVH(val binding: TileExploreFeedAllSongCoverBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { onCoverClickListener(covers[adapterPosition], adapterPosition) }
        }

        fun bind(cover: Cover) {
            binding.apply {
                ivThumbnail.loadUrl(cover.thumbnailUrl)
                tvUserName.text = cover.userMini.handle
                tvSongTitle.text = cover.songMini.title
            }
        }
    }
}