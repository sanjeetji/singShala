package com.sensibol.lucidmusic.singstr.gui.app.explore

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.Cover
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.domain.model.names
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.app.search.SearchNode
import com.sensibol.lucidmusic.singstr.gui.databinding.TileExploreSongBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import javax.inject.Inject
import kotlin.properties.Delegates

class TrendingSongAdapter @Inject constructor() : PagingDataAdapter<SongMini, TrendingSongAdapter.TrendingSongVH>(TrendingSongDiff()) {

    internal var trendingSongViews: List<ExploreSongView> by Delegates.observable(listOf()) { _, _, _ -> notifyDataSetChanged() }

    internal var onTrendingSingClickListener: (SongMini) -> Unit = { _ -> }

    internal var onTrendingCoverClickListener: (Cover, SongMini?, Int) -> Unit = { _, _, _ -> }

    class TrendingSongDiff : DiffUtil.ItemCallback<SongMini>(){
        override fun areItemsTheSame(oldItem: SongMini, newItem: SongMini): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: SongMini, newItem: SongMini): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TrendingSongVH(TileExploreSongBinding.inflate(parent.layoutInflater, parent, false))

    override fun onBindViewHolder(holder: TrendingSongAdapter.TrendingSongVH, position: Int) = holder.bind(trendingSongViews[position])


    inner class TrendingSongVH(private val binding: TileExploreSongBinding) : RecyclerView.ViewHolder(binding.root) {

        private val coversAdapter: CoversAdapter = CoversAdapter().also {
            it.onCoverClickListener = { cover, position -> onTrendingCoverClickListener(cover, trendingSongViews[adapterPosition].songMini, position) }
        }

        init {
            binding.apply {
                rvCovers.apply {
                    addItemDecoration(object : RecyclerView.ItemDecoration() {
                        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                            val startMargin = if (0 == parent.getChildAdapterPosition(view)) resources.getDimension(R.dimen.screen_horz_margin) else 0
                            outRect.set(startMargin.toInt(), 0, 0, 0)
                        }
                    })
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = coversAdapter
                }
            }
        }

        fun bind(exploreSongView: ExploreSongView) {
            binding.apply {
                incTileWithSingCta.apply {
                    ivThumbnail.loadUrl(exploreSongView.songMini.thumbnailUrl)
                    tvTitle.text = exploreSongView.songMini.title
                    tvSubtitle.text = exploreSongView.songMini.artists.names
                    tvSing.setOnClickListener {
                        Analytics.logEvent(
                            Analytics.Event.SingCtaClickEvent(
                                Analytics.Event.Param.SongId(exploreSongView.songMini.title),
                            )
                        )
                        onTrendingSingClickListener(exploreSongView.songMini)
                    }
                }
                coversAdapter.covers = exploreSongView.covers
            }
        }
    }

}