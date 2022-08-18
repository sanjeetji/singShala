package com.sensibol.lucidmusic.singstr.gui.app.profile.self.followersfollowing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.lucidmusic.singstr.domain.model.FollowersUser
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.TileFollowerViewBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import javax.inject.Inject
import kotlin.properties.Delegates

internal class FollowerListAdapter @Inject constructor() :
    RecyclerView.Adapter<FollowerListAdapter.FollowListVH>() {

    internal var onImageClickListener: (FollowersUser) -> Unit = {}

    internal var onFollowBtnClickListener: (Boolean, String) -> Unit = { check: Boolean, userId: String -> }

    var followUser = false

    internal var followerUserList: List<FollowersUser> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    inner class FollowListVH(private val binding: TileFollowerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(followerUserList: FollowersUser) {
            binding.apply {
                tvDisplayName.text = followerUserList.first_name
                tvUserName.text = followerUserList.user_handle
                cvProfilePic.loadUrl(followerUserList.image)
                userView.setOnClickListener { onImageClickListener(followerUserList) }

                if (followerUserList.subscribe) {
                    tvFollowButton.isChecked = true
                    tvFollowButton.background = ContextCompat.getDrawable(tvFollowButton.context, R.drawable.bg_rounded_dark_blue_card);
                    tvFollowButton.text = "Following"
                    tvFollowButton.setTextColor(ContextCompat.getColor(tvFollowButton.context, R.color.following_color))
                } else {
                    tvFollowButton.isChecked = false
                    tvFollowButton.background = ContextCompat.getDrawable(tvFollowButton.context, R.drawable.bg_rounded_light_blue);
                    tvFollowButton.text = "Follow"
                    tvFollowButton.setTextColor(ContextCompat.getColor(tvFollowButton.context, R.color.white))
                }

                tvFollowButton.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        tvFollowButton.background = ContextCompat.getDrawable(tvFollowButton.context, R.drawable.bg_rounded_dark_blue_card);
                        tvFollowButton.text = "Following"
                        tvFollowButton.setTextColor(ContextCompat.getColor(tvFollowButton.context, R.color.following_color))
                        onFollowBtnClickListener(true, followerUserList._id)
                    } else {
                        tvFollowButton.background = ContextCompat.getDrawable(tvFollowButton.context, R.drawable.bg_rounded_light_blue);
                        tvFollowButton.text = "Follow"
                        tvFollowButton.setTextColor(ContextCompat.getColor(tvFollowButton.context, R.color.white))
                        onFollowBtnClickListener(false, followerUserList._id)
                    }

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FollowListVH(TileFollowerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)).apply {
        }

    override fun getItemCount() = followerUserList.size

    override fun onBindViewHolder(holder: FollowListVH, position: Int) {
        holder.bind(followerUserList[position])
    }

}