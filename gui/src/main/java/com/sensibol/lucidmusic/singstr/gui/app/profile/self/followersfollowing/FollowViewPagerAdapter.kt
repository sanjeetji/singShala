package com.sensibol.lucidmusic.singstr.gui.app.profile.self.followersfollowing

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import timber.log.Timber

class FollowViewPagerAdapter(
    fragment: Fragment, userId: String
) : FragmentStateAdapter(fragment) {

    private val mUserId = userId
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        Timber.d("createFragment position: $position")
        return if (position == 0)
            FollowingFragment.newInstance(mUserId)
        else
            FollowersFragment.newInstance(mUserId)
    }

}