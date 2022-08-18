package com.sensibol.lucidmusic.singstr.gui.app.home

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.Cover
import com.sensibol.lucidmusic.singstr.gui.databinding.TileFeedCoverBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import com.sensibol.lucidmusic.singstr.gui.prettyViewsCount
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject
import kotlin.properties.Delegates

@FragmentScoped
internal class FeedCoversAdapter @Inject constructor() : RecyclerView.Adapter<FeedCoversAdapter.FeedCoverVH>() {

    internal var onCoverClickListener: (cover: Cover, position: Int) -> Unit = { _, _ -> }

    internal var covers: List<Cover> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    override fun getItemCount(): Int = covers.size

    override fun onBindViewHolder(holder: FeedCoverVH, position: Int) = holder.bind(covers[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedCoverVH =
        FeedCoverVH(TileFeedCoverBinding.inflate(parent.layoutInflater, parent, false))

    inner class FeedCoverVH(val binding: TileFeedCoverBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { onCoverClickListener(covers[adapterPosition], adapterPosition) }
        }

        fun bind(cover: Cover) {
            binding.apply {
                ivThumbnail.loadUrl(cover.thumbnailUrl)
                tvViewCount.text = prettyViewsCount(cover.statistics.viewCount)
                tvSongTitle.text = cover.songMini.title

            }
        }
    }

}