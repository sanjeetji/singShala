package com.sensibol.lucidmusic.singstr.gui.app.profile.other

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.lucidmusic.singstr.gui.app.profile.self.CoverView
import com.sensibol.lucidmusic.singstr.gui.databinding.TileCoverBinding
import com.sensibol.lucidmusic.singstr.gui.databinding.TileCoverProfileBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import com.sensibol.lucidmusic.singstr.gui.prettyViewsCount
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject
import kotlin.properties.Delegates

@FragmentScoped
internal class OtherUserCoversAdapter @Inject constructor() : RecyclerView.Adapter<OtherUserCoversAdapter.OtherUserCoversVH>() {

    internal var onCoverClickListener: (CoverView, String, Int) -> Unit = {  _, _, _ -> }

    internal var coverViews: List<CoverView> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return coverViews.size
    }

    inner class OtherUserCoversVH(private val binding: TileCoverProfileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(coverView: CoverView) {
            binding.ivThumbnail.loadUrl(coverView.thumbnailUrl)
            binding.tvSongTitle.text = coverView.title
            binding.tvViewCount.text = prettyViewsCount(coverView.viewCount)
        }
    }

    override fun onBindViewHolder(holder: OtherUserCoversVH, position: Int) = holder.bind(coverViews[position])


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OtherUserCoversVH(
        TileCoverProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ).apply {
        itemView.setOnClickListener{
            onCoverClickListener(coverViews[adapterPosition], coverViews[adapterPosition].attemptId, adapterPosition)
        }
    }
}