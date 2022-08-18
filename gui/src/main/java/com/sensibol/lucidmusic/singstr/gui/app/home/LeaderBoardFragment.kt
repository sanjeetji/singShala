package com.sensibol.lucidmusic.singstr.gui.app.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.User
import com.sensibol.lucidmusic.singstr.domain.model.UserStats
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.app.onboard.LeaderboardViewModel
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentLeaderboardBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import com.sensibol.lucidmusic.singstr.gui.loadCenterCropImageFromUrl
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LeaderBoardFragment : BaseFragment() {

    override val layoutResId = R.layout.fragment_leaderboard
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentLeaderboardBinding::inflate
    override val binding get() : FragmentLeaderboardBinding = super.binding as FragmentLeaderboardBinding

    @Inject
    internal lateinit var leaderboardAdapter: LeaderboardAdapter

    private val viewmodel: LeaderboardViewModel by viewModels()

    private val activityViewModel: SingstrViewModel by viewModels()
    private lateinit var userID: String
    private lateinit var userRank: String


    override fun onInitView() {
        viewmodel.apply {
            failure(failure, ::handleFailure)
            observe(leaderUserList, { leaderboardAdapter.collections = it })
            observe(leaderUserRank, ::showUserRank)
            fetchLeaderUserList()
            getUserRank()
        }

        activityViewModel.apply {
            failure(failure, ::handleFailure)
            observe(userStats, ::showUserStats)
            observe(user, ::showUserProfile)
            loadUserStats()
            loadUserProfile()
        }

        binding.apply {
            tvHowToEarnXP.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.LearnEarnXpEvent(Analytics.Event.Param.UserId(userID)))
                findNavController().navigate(LeaderBoardFragmentDirections.toEarnXpFragment())
            }
            RankedList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = leaderboardAdapter
            }
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            leaderboardAdapter.onUserClickListener = {
                if (it.userId == userID) {
                    findNavController().popBackStack()
                } else {
                    findNavController().navigate(LeaderBoardFragmentDirections.toOtherUserProfileFragment(it.userId))
                }
            }
            tvRewards.setOnClickListener{
                Analytics.logEvent(
                    Analytics.Event.CheckRewardsEvent(Analytics.Event.Param.UserId(userID))
                )
                val urlLink = "https://singshala.com/rewards/"
                findNavController().navigate(LeaderBoardFragmentDirections.toWebViewFragment(urlLink))
            }
        }
    }

    private fun showUserRank(rank: Int) {
        Analytics.setUserProperty(Analytics.UserProperty.UserGlobalRank(rank))
        binding.apply {
            rankUser.text = rank.toString()
            Analytics.logEvent(
                Analytics.Event.CheckLeaderboardEvent(
                    Analytics.Event.Param.UserRank(rank)
                )
            )
        }
    }

    private fun showUserProfile(user: User) {
        userID = user.id
        userRank = activityViewModel.user.value!!.name
        binding.apply {
            ivFragMainUserImage.loadCenterCropImageFromUrl(user.dpUrl)
            ivProfilePicture.loadUrl(user.dpUrl)
            tvSingstrName.text = user.name.capitalize(Locale.ROOT)
        }
    }

    private fun showUserStats(userStats: UserStats) {
        val additionalXPs = "You require an additional ${userStats.remainingNextXp} XP to level up!"
        binding.apply {
            tvInfoXP.text = additionalXPs
            tvXP.text = "XP " + userStats.totalXp.toString()
            tvXPRanked.text = "XP " + userStats.totalXp.toString()
            pbLevelProgress.progress = userStats.level
            tvCurrentLvl.text = userStats.level.toString()
            tvNextLevelNo.text = userStats.nextLevel.toString()
            tvLevelNo.text = userStats.level.toString()
            tvLeveNo.text = userStats.level.toString()
            tvNextLevelXPReq.text = userStats.remainingNextXp.toString() + " XP"
        }
    }

}