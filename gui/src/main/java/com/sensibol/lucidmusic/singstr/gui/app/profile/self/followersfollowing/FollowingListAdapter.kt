package com.sensibol.lucidmusic.singstr.gui.app.profile.self.followersfollowing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.lucidmusic.singstr.domain.model.FollowingUser
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.TileFollowingViewBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import javax.inject.Inject
import kotlin.properties.Delegates

internal class FollowingListAdapter @Inject constructor() :
    RecyclerView.Adapter<FollowingListAdapter.FollowListVH>() {

    internal var onImageClickListener: (FollowingUser) -> Unit = {}
    internal var onFollowingBtnClickListener: (Boolean, String) -> Unit = { check: Boolean, userId: String -> }

    internal var followingUserList: List<FollowingUser> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    inner class FollowListVH(private val binding: TileFollowingViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(followingUserList: FollowingUser) {
            binding.apply {
                tvDisplayName.text = followingUserList.first_name
                tvUserName.text = followingUserList.user_handle
                cvProfilePic.loadUrl(followingUserList.image)
                userView.setOnClickListener {
                    onImageClickListener(followingUserList)
                }

                if (followingUserList.subscribe) {
                    tvFollowingButton.isChecked = true
                    tvFollowingButton.background = ContextCompat.getDrawable(tvFollowingButton.context, R.drawable.bg_rounded_dark_blue_card);
                    tvFollowingButton.text = "Following"
                    tvFollowingButton.setTextColor(ContextCompat.getColor(tvFollowingButton.context, R.color.following_color))
                }
                else{
                    tvFollowingButton.isChecked = false
                    tvFollowingButton.background = ContextCompat.getDrawable(tvFollowingButton.context, R.drawable.bg_rounded_light_blue);
                    tvFollowingButton.text = "Follow"
                    tvFollowingButton.setTextColor(ContextCompat.getColor(tvFollowingButton.context, R.color.white))
                }

                tvFollowingButton.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        tvFollowingButton.background = ContextCompat.getDrawable(tvFollowingButton.context, R.drawable.bg_rounded_dark_blue_card);
                        tvFollowingButton.text = "Following"
                        tvFollowingButton.setTextColor(ContextCompat.getColor(tvFollowingButton.context, R.color.following_color))
                        onFollowingBtnClickListener(true, followingUserList._id)
                    } else {
                        tvFollowingButton.background = ContextCompat.getDrawable(tvFollowingButton.context, R.drawable.bg_rounded_light_blue);
                        tvFollowingButton.text = "Follow"
                        tvFollowingButton.setTextColor(ContextCompat.getColor(tvFollowingButton.context, R.color.white))
                        onFollowingBtnClickListener(false, followingUserList._id)
                    }

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FollowListVH(TileFollowingViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)).apply {
        }

    override fun getItemCount() = followingUserList.size

    override fun onBindViewHolder(holder: FollowListVH, position: Int) {
        holder.bind(followingUserList[position])
    }

}