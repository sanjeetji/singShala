package com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.self

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.OtherUserProfile
import com.sensibol.lucidmusic.singstr.domain.model.SingMode
import com.sensibol.lucidmusic.singstr.gui.*
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.app.profile.other.OtherUserProfileFragmentArgs
import com.sensibol.lucidmusic.singstr.gui.app.profile.self.ProfileFragmentDirections
import com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.TeacherDetailsViewModel
import com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.TeacherProfessionalDetailsAdapter
import com.sensibol.lucidmusic.singstr.gui.checkUserHandle
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentTeacherDetailsBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TeacherDetailsFragment : BaseFragment() {
    override val layoutResId: Int get() = R.layout.fragment_teacher_details
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding get() = FragmentTeacherDetailsBinding::inflate
    override val binding: FragmentTeacherDetailsBinding get() = super.binding as FragmentTeacherDetailsBinding

    val args: TeacherDetailsFragmentArgs by navArgs()

    private val teacherDetailsViewModel: TeacherDetailsViewModel by viewModels()

    var userSubscription = false
    var teacherId = "";
    var loggedInUserId = "";
    var teacherHandle = ""


    @Inject
    lateinit var teacherProfessionalDetailsAdapter: TeacherProfessionalDetailsAdapter

    override fun onInitView() {
        Timber.e("========= Teacher id is in TeacherDetailsFragment: "+args.teacherId)

        teacherDetailsViewModel.apply {
            failure(failure, ::handleFailure)
            observe(teacherProfile,:: showTeacherProfile)
            //TODO change static teacher id
            loadTeacherProfile("61d403a3cc90df1783551e9e") // for testing purpose after that we need to dynamic teacher Id
            observe(leaderTeacherRank, :: showTeacherRank)
            getTeacherRank()
        }

        teacherProfessionalDetailsAdapter = TeacherProfessionalDetailsAdapter();

        binding.apply {

            pbLevel.isIndeterminate = false
            vpTeacherSelf.isUserInputEnabled = false
            vpTeacherSelf.adapter = TeacherSelfPagerAdapter(this@TeacherDetailsFragment,args.teacherId)

            for (i in 0 until tabLayout.tabCount) {
                val tabView: View = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
                tabView.requestLayout()
                ViewCompat.setPaddingRelative(tabView, 50, tabView.paddingTop, 50, tabView.paddingBottom)
            }
            TabLayoutMediator(tabLayout, vpTeacherSelf) { tab, position ->
                when (position) {
                    0 -> {
                        tabLayout.setTabTextColors(Color.parseColor("#727272"), Color.parseColor("#ffffff"))
                        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#ffffff"));
                        tab.text = getText(R.string.lesson)
                        tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.selector_school_icon);
                    }
                    1 -> {
                        tab.text = getText(R.string.covers)
                        tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_music_node);
                    }
                    else -> {
                        tab.text = getText(R.string.bio)
                        tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_profile);
                    }
                }
                tab.select()
            }.attach()

            ivThreeDots.setOnClickListener {
                findNavController().navigate(TeacherDetailsFragmentDirections.toSettingsFragment())
            }

            ivMicSong.setOnClickListener {
                findNavController().navigate(TeacherDetailsFragmentDirections.toPrepareSingHostFragment(SingMode.PRACTICE.argName))
            }
        }
    }

    private fun showTeacherRank(rank: Int) {
        binding.apply {
            tvRankScore.text = "RANK #$rank"
            if(rank<=100){
                clRank.visibility = View.VISIBLE
                tvRank.text = rank.toString()
            }else
                clRank.visibility = View.GONE
        }
    }

    private fun showTeacherProfile(otherUserProfile: OtherUserProfile) {
        binding.apply {
            teacherId = otherUserProfile.user.id
            teacherHandle = otherUserProfile.user.handle.checkUserHandle()
            tvHandle.text = teacherHandle
            tvDisplayName.text = otherUserProfile.user.name
            tvCoverNumber.text = otherUserProfile.stats.coversCount.toString()
            tvFollwingNumber.text = otherUserProfile.stats.subscribersCount.toString()
            tvFollwersNumber.text = otherUserProfile.stats.subscription.toString()
            tvUserStatus.text = otherUserProfile.user.status
            if (otherUserProfile.user.city == "" && otherUserProfile.user.state == "") {
                tvLocation.visibility = View.INVISIBLE
            } else if (otherUserProfile.user.city == "") {
                tvLocation.text = otherUserProfile.user.state
            } else if (otherUserProfile.user.state == "") {
                tvLocation.text = otherUserProfile.user.city
            } else {
                tvLocation.text = "${otherUserProfile.user.city}, ${otherUserProfile.user.state}"
            }
            cvProfilePic.loadCenterCropImageFromUrl(otherUserProfile.user.dpUrl)
            tvLevelScore.text = "LVL ${otherUserProfile.stats.level}"
        }
    }
}