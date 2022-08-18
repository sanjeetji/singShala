package com.sensibol.lucidmusic.singstr.gui.app.profile.self

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.TileCoverBinding
import com.sensibol.lucidmusic.singstr.gui.databinding.TileCoverProfileBinding
import com.sensibol.lucidmusic.singstr.gui.databinding.TileCreateCoverBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import com.sensibol.lucidmusic.singstr.gui.prettyViewsCount
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject
import kotlin.properties.Delegates

@FragmentScoped
internal class CoversAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    internal var onCreateCoverClickListener: () -> Unit = {}
    internal var onSavedDraftsClickListener: () -> Unit = {}
    internal var onCoverClickListener: (CoverView, String, Int) -> Unit = { _, _, _ -> }
    internal var onCoverDeleteListener: (CoverView) -> Unit = {}
    internal var draftCount:Int = 0
    internal var totalXp:Int = 0

    internal var coverViews: List<CoverView> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    companion object {
        private const val TYPE_CREATE_COVER = 0
        private const val TYPE_COVER = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_CREATE_COVER
            else -> TYPE_COVER
        }
    }

    override fun getItemCount(): Int {
        return coverViews.size + 1
    }

    internal abstract class BaseViewHolder<T>(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        abstract fun bind()
    }

    private inner class CreateCoverViewHolder(private var binding: TileCreateCoverBinding) : BaseViewHolder<CoverView>(binding) {
        override fun bind() {
            binding.tvSingButton.setOnClickListener{
                onCreateCoverClickListener()
            }
            val draftCount =if(draftCount>0) {draftCount.toString()} else {"0"}
            binding.draftsCount.text = "$draftCount Draft Pending"
            binding.tvTotalXP.text= "+$totalXp XP"
            binding.viewSaveDraft.setOnClickListener{
                onSavedDraftsClickListener()
            }
            Analytics.setUserProperty(Analytics.UserProperty.PublishedCovers(coverViews.size))
        }
    }

    private inner class CoverViewHolder(private var binding: TileCoverProfileBinding) : BaseViewHolder<CoverView>(binding) {
        override fun bind() {
            binding.ivThumbnail.loadUrl(coverViews[adapterPosition - 1].thumbnailUrl)
            binding.tvSongTitle.text = coverViews[adapterPosition-1].title
            binding.tvViewCount.text = prettyViewsCount(coverViews[adapterPosition-1].viewCount)
            itemView.setOnClickListener {
                onCoverClickListener(coverViews[adapterPosition - 1], coverViews[adapterPosition - 1].attemptId, adapterPosition - 1)
            }
            itemView.setOnLongClickListener {
                onCoverDeleteListener(coverViews[adapterPosition - 1])
                true
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CreateCoverViewHolder -> holder.bind()
            is CoverViewHolder -> holder.bind()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_CREATE_COVER -> CreateCoverViewHolder(TileCreateCoverBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> CoverViewHolder(TileCoverProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }
}