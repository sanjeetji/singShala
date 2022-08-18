package com.sensibol.lucidmusic.singstr.gui.app.explore

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.Cover
import com.sensibol.lucidmusic.singstr.gui.databinding.TileExploreFeedCoverBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import javax.inject.Inject
import kotlin.properties.Delegates

internal class CoversAdapter @Inject constructor() : RecyclerView.Adapter<CoversAdapter.ExploreSongChildVH>() {

    internal var covers: List<Cover> by Delegates.observable(listOf()) { _, _, _ -> notifyDataSetChanged() }

    internal var onCoverClickListener: (Cover, Int) -> Unit = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreSongChildVH = ExploreSongChildVH(
        TileExploreFeedCoverBinding.inflate(parent.layoutInflater, parent, false)
    )

    override fun onBindViewHolder(holder: ExploreSongChildVH, position: Int) = holder.bind(covers[position])

    override fun getItemCount(): Int = covers.size

    inner class ExploreSongChildVH(private val binding: TileExploreFeedCoverBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { onCoverClickListener(covers[adapterPosition], adapterPosition) }
        }

        @SuppressLint("SetTextI18n")
        fun bind(cover: Cover) {
            binding.apply {
                ivThumbnail.loadUrl(cover.thumbnailUrl)
                tvUserName.text = cover.userMini.handle
                tvRank.text = "#" + (adapterPosition + 1).toString()
            }
        }
    }
}