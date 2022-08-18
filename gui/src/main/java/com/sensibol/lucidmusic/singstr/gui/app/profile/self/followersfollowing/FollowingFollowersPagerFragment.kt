package com.sensibol.lucidmusic.singstr.gui.app.profile.self.followersfollowing

import android.graphics.Color
import android.view.*
import android.view.View.*
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentFollowingFollowingPagerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class FollowingFollowersPagerFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_following_following_pager
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) ->
    ViewBinding = FragmentFollowingFollowingPagerBinding::inflate
    override val binding
        get():FragmentFollowingFollowingPagerBinding =
            super.binding as FragmentFollowingFollowingPagerBinding

    private val args: FollowingFollowersPagerFragmentArgs by navArgs()

    override fun onInitView() {

        binding.apply {

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            tvHandle.text = args.username
            followPager.isUserInputEnabled = false
            followPager.adapter = FollowViewPagerAdapter(this@FollowingFollowersPagerFragment, args.userId)

            for (i in 0 until tabLayout.tabCount) {
                val tabView: View = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
                tabView.requestLayout()
                ViewCompat.setPaddingRelative(tabView, 50, tabView.paddingTop, 50, tabView.paddingBottom)
            }
            TabLayoutMediator(tabLayout, followPager) { tab, position ->
                if (position == 0) {
                    tabLayout.setTabTextColors(Color.parseColor("#727272"), Color.parseColor("#ffffff"))
                    tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#ffffff"));
                    tab.text = "Following"
                } else if (position == 1) {
                    tab.text = "Followers"
                }
                tab.select()
            }.attach()
            followPager.post(
                Runnable {
                    binding.followPager.setCurrentItem(
                        if (args.goToSecondTab) 1
                        else
                            0
                    )
                }
            )
        }
    }

    public fun setFollowingCount(size: Int) {
        binding.tabLayout.getTabAt(0)?.text = "$size Following"
    }

    public fun setFollowerCount(size: Int) {
        binding.tabLayout.getTabAt(1)?.text = "$size Followers"
    }

    public fun openUserProfile(userId: String) {
        findNavController().navigate(FollowingFollowersPagerFragmentDirections.toOtherUserProfileFragment(userId))
    }

}