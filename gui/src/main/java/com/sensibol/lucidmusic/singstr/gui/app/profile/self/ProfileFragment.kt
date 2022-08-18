package com.sensibol.lucidmusic.singstr.gui.app.profile.self

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.*
import android.view.View.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.app.profile.TeacherDetailsPagerAdapter
import com.sensibol.lucidmusic.singstr.gui.app.util.loadCenterCropImageFromUrl
import com.sensibol.lucidmusic.singstr.gui.convertDatePatternWebEngage
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentProfileBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import com.sensibol.lucidmusic.singstr.gui.prettyViewsCount
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
internal class ProfileFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_profile
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentProfileBinding::inflate
    override val binding get():FragmentProfileBinding = super.binding as FragmentProfileBinding

    private val viewModel: ProfileViewModel by viewModels()

    private val singstrViewModel: SingstrViewModel by activityViewModels()

    private lateinit var teacherDetailsPagerAdapter: TeacherDetailsPagerAdapter

    @Inject
    internal lateinit var coversAdapter: CoversAdapter

    private lateinit var userId: String
    private lateinit var userName: String

    override fun onInitView() {
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(profileView, ::showProfile)
            observe(coverViews, ::showCoverViews)
            observe(deleteCover, ::showDeleteCoverResult)
            observe(leaderUserRank, ::showUserRank)
//            observe(draftsPage, ::showUserDraft)
            observe(nodeDraft, ::handleNodeDraft)
            observe(isTeacher, ::handleIsTeacher)
            getProfile()
            getUserStats()
            getUserRank()
//            getDrafts()
        }

        singstrViewModel.apply {
            failure(failure, ::handleFailure)
            observe(userStats, ::showUserStats)
            observe(userSubscription, ::showUserSubscription)
            loadUserStats()
        }

        coversAdapter.onCreateCoverClickListener = {
            moveToSingScreen()
        }

        coversAdapter.onSavedDraftsClickListener = {
            moveToDraftListScreen()
        }

        coversAdapter.onCoverDeleteListener = { it ->
            showDeleteConfirmationDialog(it.attemptId, it.title)
        }

        binding.apply {
            viewFollower.isEnabled = false
            viewFollowing.isEnabled = false
            tabTeacherVp.visibility = GONE
            vpTeacher.visibility = GONE
//            groupTeacherDetails.visibility = GONE
            rvProfile.visibility = GONE
            groupRank.visibility = GONE
            progressBar.visibility = VISIBLE
            incPracticeNow.root.visibility = GONE
            btnBecomePro.visibility = GONE
            incPracticeNow.pbPracticeRemainingTime.max = SystemProperties.MAX_PRACTICE_DURATION_MS

            rvProfile.apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = coversAdapter
            }

            viewFollowing.setOnClickListener {
                findNavController().navigate(
                    ProfileFragmentDirections.toFollowingFollowersPagerFragment(
                        false, userName, userId
                    )
                )
            }

            viewFollower.setOnClickListener {
                findNavController().navigate(
                    ProfileFragmentDirections.toFollowingFollowersPagerFragment(
                        true, userName, userId
                    )
                )
            }

            ivThreeDots.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.toSettingsFragment())
            }

            ivMicSong.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.toPrepareSingHostFragment(SingMode.PRACTICE.argName))
            }

            incPracticeNow.tvPracticeButton.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.toPrepareSingHostFragment(SingMode.PRACTICE.argName))
            }

            btnBecomePro.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.ProfileProCTAEvent(
                        Analytics.Event.Param.UserId(userId)
                    )
                )
                findNavController().navigate(ProfileFragmentDirections.toProSubscriptionPlanFragment())
            }

            vRank.setOnClickListener {
                findNavController().navigate(
                    ProfileFragmentDirections.toLeaderBoardFragment()
                )
            }

        }
    }

    private fun showUserRank(rank: Int) {
        binding.apply {
//            if (rank <= 100) {
//                groupRank.visibility = VISIBLE
//                tvRank.text = rank.toString()
//            } else
//                groupRank.visibility = GONE


            groupRank.visibility = VISIBLE
            tvRankMatrix.visibility = GONE
            tvRank.text = rank.toString()
        }
    }

    private fun showUserSubscription(proSubscription: ProSubscription) {
        Analytics.setUserProperty(Analytics.UserProperty.UserPlan(if (proSubscription.subscribed) "pro" else "free"))
        Analytics.setUserProperty(Analytics.UserProperty.UserRenewalDate(convertDatePatternWebEngage(proSubscription.validity)))
        if (proSubscription.subscribed) {
            binding.ivProTag.visibility = VISIBLE
            binding.btnBecomePro.visibility = GONE
        } else {
            binding.ivProTag.visibility = GONE
            binding.btnBecomePro.visibility = VISIBLE
        }
    }

    internal fun showDeleteConfirmationDialog(attemptId: String, songTitle: String) {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_delete_your_cover, null)
        val mBuilder = AlertDialog.Builder(activity).setView(dialogView)
        val deleteCover = dialogView.findViewById<TextView>(R.id.tvDelete)
        val keepCover = dialogView.findViewById<TextView>(R.id.btnNo)
        val alertDialog = mBuilder.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteCover.setOnClickListener {
            viewModel.deleteCover(attemptId)
            alertDialog.dismiss()
            Analytics.logEvent(
                Analytics.Event.DeleteCoverEvent(
                    Analytics.Event.Param.SongId(songTitle),
                    Analytics.Event.Param.GenreId("NA"),
                    Analytics.Event.Param.ArtistId("NA"),
                )
            )
        }
        keepCover.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun showUserStats(userStats: UserStats) {
        Analytics.setUserProperty(Analytics.UserProperty.UserSubscribers(userStats.subscription))
        binding.apply {
            pbLevel.isIndeterminate = false
            (userStats.totalXp + userStats.remainingNextXp).let { nextLevelXp ->
                if (0 == nextLevelXp) {
                    pbLevel.progress = 0
                } else {
                    pbLevel.setProgress(userStats.remainingNextXp * 100 / nextLevelXp, true)
                }
            }
            tvLevelScore.text = "LVL ${userStats.level}"
            tvFollwingNumber.apply {
                visibility = INVISIBLE
                text = prettyViewsCount(userStats.subscribersCount.toLong())
            }
            tvFollwersNumber.apply {
                visibility = INVISIBLE
                text = prettyViewsCount(userStats.subscription.toLong())
            }

            incPracticeNow.apply {
                userStats.remainingPracticeTimeMS.let {
                    val practiceTime = SystemProperties.MAX_PRACTICE_DURATION_MS - it
                    pbPracticeRemainingTime.apply {
                        progress = if (practiceTime < 6000)
                            500
                        else
                            practiceTime
                    }
                    tvFrgMainPracticeScore.text = (practiceTime / (1_000 * 60)).toString()
                }
                val practiceXPLabel = SpannableStringBuilder(getString(R.string.practice_for_15_mins_to_get_100xp))
                practiceXPLabel.setSpan(
                    ForegroundColorSpan(Color.parseColor("#5DB427")),
                    28,
                    practiceXPLabel.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tvPracticeXpLabel.text = practiceXPLabel
            }
//            coversAdapter.draftCount = userStats.draftsCount
//            coversAdapter.totalXp = userStats.pendingXP
        }
    }

    private fun showProfile(profileView: ProfileView) {
        userId = profileView.id
        userName = profileView.username
//        viewModel.getUserDraftList(userId)

        viewModel.checkTeacher(userId)
        binding.apply {
            viewFollower.isEnabled = true
            viewFollowing.isEnabled = true

            Analytics.logEvent(
                Analytics.Event.UserProfileViewEvent(
                    Analytics.Event.Param.UserId("self")
                )
            )

//            container.visibility = VISIBLE
            tvDisplayName.text = profileView.name
            tvHandle.text = profileView.username
            tvUserStatus.apply {
                val userStatus = profileView.status
                text = userStatus
                if (userStatus.isEmpty()) {
                    visibility = GONE
                }
            }
            tvLocation.apply {
                val userStatus = profileView.location
                text = userStatus
                if (userStatus.isEmpty()) {
                    visibility = GONE
                }
            }

            cvProfilePic.loadCenterCropImageFromUrl(profileView.profileURL)
            tvEditProfile.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.EditProfileEvent(Analytics.Event.Param.UserId(userId))
                )
                findNavController().navigate(ProfileFragmentDirections.toEditProfileFragment(profileView))
            }
            tvLevelScore.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.toLeaderBoardFragment())
            }
            coversAdapter.onCoverClickListener = { coverView, _, position ->
                moveToUserFeedScreen(coverView, position)
            }
        }
    }

    private fun showCoverViews(coverViews: List<CoverView>) {
//        if (coverViews.isEmpty() || coverViews.size == 1) {
//            binding.rvProfile.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
//        }
        coversAdapter.coverViews = coverViews
        showUserStatics(coverViews.size)
    }

    internal fun showUserStatics(coverCount: Int){
        Timber.d("showUserStatus IN")
        binding.apply {
            progressBar.visibility = GONE
            tvCoverNumber.apply {
                text = prettyViewsCount(coverCount.toLong())
                visibility = VISIBLE
            }
            tvFollwersNumber.visibility = VISIBLE
            tvFollwingNumber.visibility = VISIBLE
        }
    }

    private fun showDeleteCoverResult(isCoverDeleted: Boolean) {
        if (isCoverDeleted) {
            Toast.makeText(context, "Cover Deleted Successfully", Toast.LENGTH_SHORT).show()
            viewModel.getCovers()
        }
    }

    private fun openPopupMenu(v: View) {
        context?.let {
            val inflater = it.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupview = inflater.inflate(R.layout.popup_user_profile, null)
            PopupWindow(popupview, 380, RelativeLayout.LayoutParams.WRAP_CONTENT, true)
                .showAsDropDown(v, -50, -50, Gravity.START)
        }
        /*context.let {
            ContextThemeWrapper(it, R.style.PopupMenu).apply {
                PopupMenu(this, v, Gravity.CENTER).apply {
                    setOnMenuItemClickListener(OnPopupMenuItemClick())
                    menuInflater.inflate(R.menu.profile_menu, menu)
                    show()
                }
            }
        }*/
    }

//    private fun showUserDraft(draftScoreList: List<CoverOrDraftScore>){
//        coversAdapter.draftCount = draftScoreList.size
//        var totalXp = 0
//        draftScoreList.forEach {
//            totalXp += it.totalScore
//        }
//        coversAdapter.totalXp = totalXp
//        viewModel.getCovers()
//    }

    private fun showUserDraft(draftsPage: DraftsPage) {
        coversAdapter.draftCount = draftsPage.drafts.size
        var totalXp = 0
        draftsPage.drafts.forEach {
            totalXp += it.totalXP.toInt()
        }
        coversAdapter.totalXp = totalXp
        viewModel.getCovers()
    }

    class OnPopupMenuItemClick : PopupMenu.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            return true
        }

    }

    private fun handleIsTeacher(checkTeacher: CheckTeacher){
        if(checkTeacher.teacherId == "false"){
            binding.apply {
                tabTeacherVp.visibility = GONE
                vpTeacher.visibility = GONE
                rvProfile.visibility = VISIBLE
                tvTeacherTag.visibility = GONE
                incPracticeNow.root.visibility = VISIBLE
            }
            singstrViewModel.getUserSubscription()
        }else{
            binding.apply {
                rvProfile.visibility = GONE
                tvTeacherTag.visibility = VISIBLE
                ivProTag.visibility = GONE
                btnBecomePro.visibility = GONE
                incPracticeNow.root.visibility = GONE
            }
            showTeacherDetails(checkTeacher.teacherId)
        }
        viewModel.getNodeDraftList(userId)
    }

    private fun showTeacherDetails(teacherId: String) {
        teacherDetailsPagerAdapter = TeacherDetailsPagerAdapter(this, teacherId, userId)
        binding.apply {
            vpTeacher.apply {
                adapter = teacherDetailsPagerAdapter
                isUserInputEnabled = false
//                post {
//                    vpTeacher.currentItem = 1
//                }
            }

            TabLayoutMediator(tabTeacherVp, vpTeacher) { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = getText(R.string.lesson)
                        tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.selector_school_icon_for_teacher);
                    }
                    1 -> {
                        tab.text = getText(R.string.covers)
                        tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.selector_music_node_icon_for_teacher);
                    }
                    2 -> {
                        tab.text = getText(R.string.bio)
                        tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.selector_person_icon_for_teacher);
                    }
                }
                tab.select()
            }.attach()
            tabTeacherVp.visibility = VISIBLE
            vpTeacher.visibility = VISIBLE
            progressBar.visibility = GONE
        }
    }

    internal fun moveToDraftListScreen(){
        Analytics.logEvent(
            Analytics.Event.CheckDraftsEvent(
                Analytics.Event.Param.UserId(userId)
            )
        )
        Navigation.findNavController(binding.root).navigate(ProfileFragmentDirections.toCoversDraftFragment())
    }

    internal fun moveToSingScreen(){
        Navigation.findNavController(binding.root).navigate(ProfileFragmentDirections.toPrepareSingHostFragment(SingMode.RECORD.argName))
    }

    internal fun moveToLessonListScreen(conceptId: String){
        findNavController().navigate(ProfileFragmentDirections.toConceptLessonListFragment(conceptId))
    }

    internal fun moveToLessonScreen(lessonId: String){
        findNavController().navigate(ProfileFragmentDirections.toLearnDetailFragment(lessonId))
    }

    internal fun moveToUserFeedScreen(coverView: CoverView, position: Int){
        Analytics.logEvent(
            Analytics.Event.ViewOwnCoverEvent(
                Analytics.Event.Param.SongId(coverView.title),
                Analytics.Event.Param.ArtistId("NA"),
                Analytics.Event.Param.CoverId(coverView.attemptId),
                Analytics.Event.Param.UserId(userName),
            )
        )
        findNavController().navigate(ProfileFragmentDirections.toUserFeedFragment(userId, position))
    }

    private fun handleNodeDraft(nodeDrafts: List<NodeDraft>){
        coversAdapter.draftCount = nodeDrafts.size
        var totalXp = 0
        nodeDrafts.forEach {
            totalXp += it.totalScore
        }
        coversAdapter.totalXp = totalXp
        viewModel.getCovers()
    }

}