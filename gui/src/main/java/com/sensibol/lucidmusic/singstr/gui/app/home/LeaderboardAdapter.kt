package com.sensibol.lucidmusic.singstr.gui.app.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.lucidmusic.singstr.domain.model.LeaderboardUser
import com.sensibol.lucidmusic.singstr.gui.databinding.TileLeaderboardRankBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import javax.inject.Inject
import kotlin.properties.Delegates

class LeaderboardAdapter @Inject constructor() :
    RecyclerView.Adapter<LeaderboardAdapter.TopSingstrsVH>() {

    var onUserClickListener: (LeaderboardUser) -> Unit = {}

    var collections: List<LeaderboardUser> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    inner class TopSingstrsVH(private val binding: TileLeaderboardRankBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(leaderboardUser: LeaderboardUser) {
            binding.apply {
                ivProfilePicture.loadUrl(leaderboardUser.profileImg)
                tvSingstrName.text = leaderboardUser.firstName
                tvXPRanked.text = "XP ${leaderboardUser.xp}"
                tvLevelNo.text = leaderboardUser.level.toString()
                tvRank.text = leaderboardUser.rank.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TopSingstrsVH(TileLeaderboardRankBinding.inflate(LayoutInflater.from(parent.context), parent, false)).apply {
            itemView.setOnClickListener {
                onUserClickListener(collections[adapterPosition])
            }
        }


    override fun getItemCount() = collections.size

    override fun onBindViewHolder(holder: TopSingstrsVH, position: Int) {
        holder.bind(collections[position])
    }
}