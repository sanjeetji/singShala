package com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.self

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.profile.self.CoverView
import com.sensibol.lucidmusic.singstr.gui.databinding.TileCoverProfileBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject
import kotlin.properties.Delegates

@FragmentScoped
class TeacherSelfCoversAdapter @Inject constructor() : RecyclerView.Adapter<TeacherSelfCoversAdapter.TeacherSelfCoverVH>() {

    internal var onClickListener: () -> Unit = {}

    internal var coverViews: List<CoverView> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }


    class TeacherSelfCoverVH(private val binding: TileCoverProfileBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(coverView: CoverView) {
            binding.ivThumbnail.loadUrl(coverView.thumbnailUrl)
            binding.tvViewCount.text = coverView.viewCount.toString()
            binding.tvSongTitle.text = coverView.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TeacherSelfCoverVH(
        TileCoverProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: TeacherSelfCoverVH, position: Int) = holder.bind(coverViews[position])

    override fun getItemCount() = coverViews.size


}