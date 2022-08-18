package com.sensibol.lucidmusic.singstr.gui.app.feed

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.displayName
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.gui.databinding.TileFeedPlayerBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import timber.log.Timber
import javax.inject.Inject

internal class FeedSlotsAdapter @Inject constructor() : RecyclerView.Adapter<FeedSlotsAdapter.FeedSlotVH>() {

    internal fun addFeedSlots(feedSlots: List<FeedSlot>) {
        Timber.d("addFeedSlotViews: feedSlotViews=$feedSlots")
        val positionStart = this.feedSlots.size
        this.feedSlots.addAll(feedSlots)
        notifyItemRangeInserted(positionStart, feedSlots.size)
    }

    internal fun replaceFeedSlots(feedSlots: List<FeedSlot>){
        val mutableFeed = mutableListOf<FeedSlot>()
        mutableFeed.addAll(feedSlots)
        this.feedSlots = mutableFeed
        notifyDataSetChanged()
    }

    internal var feedSlots: MutableList<FeedSlot> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedSlotVH =
        FeedSlotVH(TileFeedPlayerBinding.inflate(parent.layoutInflater, parent, false)).also {
            Timber.d("onCreateViewHolder: ${it.displayName}")
        }

    override fun onBindViewHolder(holder: FeedSlotVH, position: Int) {
        holder.bind(feedSlots[position])
    }

    override fun getItemCount(): Int = feedSlots.size

    internal fun findItemIndexForCoverIndex(coverIndex: Int): Int {
        Timber.d("findItemIndexForCoverIndex: $coverIndex")
        var coverSlotsCount = 0
        for ((itemIndex, feedSlot) in feedSlots.withIndex()) {
            if (feedSlot is CoverSlot) {
                if (coverSlotsCount == coverIndex) {
                    Timber.d("findItemIndexForCoverIndex: $coverIndex -> [$itemIndex]")
                    return itemIndex
                } else {
                    coverSlotsCount++
                }
            }
        }
        Timber.d("findItemIndexForCoverIndex: $coverIndex not found")
        return -1
    }

    internal inner class FeedSlotVH(val binding: TileFeedPlayerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(feedSlot: FeedSlot) {
            Timber.d("bind: $adapterPosition -> $displayName : [$feedSlot]")

            binding.apply {
                ivThumbnail.apply {
                    loadUrl(feedSlot.thumbnailUrl)
                    alpha = 1f
                }
            }
        }
    }

}
