package com.sensibol.lucidmusic.singstr.gui.app.profile.other

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View.*
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.CheckTeacher
import com.sensibol.lucidmusic.singstr.domain.model.OtherUserProfile
import com.sensibol.lucidmusic.singstr.gui.*
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.app.profile.self.CoverView
import com.sensibol.lucidmusic.singstr.gui.app.profile.self.ProfileFragmentDirections
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentUserOtherProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
internal class OtherUserProfileFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_user_other_profile
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentUserOtherProfileBinding::inflate
    override val binding: FragmentUserOtherProfileBinding get() = super.binding as FragmentUserOtherProfileBinding

    private val viewModel: OtherUserProfileViewModel by viewModels()

    private val singstrViewModel: SingstrViewModel by activityViewModels()

    private var mVpPosition: Int = 0

    private lateinit var teacherDetailsPagerAdapter :OtherTeacherDetailsPagerAdapter

    @Inject
    internal lateinit var otherUserCoversAdapter: OtherUserCoversAdapter

    val args: OtherUserProfileFragmentArgs by navArgs()
    var userSubscription = false
    var userId = "";
    var loggedInUserId = "";
    var userHandle = ""

    override fun onInitView() {
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(otherUserProfile, ::showOtherUserProfile)
            observe(coverViews, ::showOtherUserCovers)
            observe(isSubscribed, ::handleUserSubscription)
            observe(reportUser, ::showReportUserResult)
            observe(isTeacher, ::handleIsTeacher)

            loadOtherUserProfile(args.userId)
//            loadOtherUserSubmits(args.userId)
        }

        binding.apply {
            otherUserCoversAdapter.onCoverClickListener = { _, _, position ->
                moveToUserFeedScreen(position)
            }

            rvProfile.apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = otherUserCoversAdapter
            }

            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }

            ivThreeDots.setOnClickListener {
                val wrapper: Context = ContextThemeWrapper(requireContext(), R.style.popUpMenuStyle)
                val popupMenu = PopupMenu(wrapper, it)
                popupMenu.menuInflater.inflate(R.menu.profile_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when (item.itemId) {
//                        R.id.message ->
                        // TODO Need ro add click actions
                        R.id.shareProfile -> shareProfile()
                        R.id.reportUser -> showReportUser()
//                        R.id.blockUser ->
                        // TODO Need ro add click actions
                    }
                    true
                })
                popupMenu.show()
            }

            viewFollowing.setOnClickListener {
                findNavController().navigate(
                    OtherUserProfileFragmentDirections.toFollowingFollowersPagerFragment(
                    false, userHandle, userId)
                )
            }

            viewFollower.setOnClickListener {
                findNavController().navigate(
                    ProfileFragmentDirections.toFollowingFollowersPagerFragment(
                    true, userHandle, userId)
                )
            }
        }
    }

    private fun showOtherUserCovers(list: List<CoverView>) {
        otherUserCoversAdapter.coverViews = list
        showUserStatics(list.size)
    }

    private fun showReportUserResult(isUserReported: Boolean) {
        Timber.d("showReportUserResult: Reported $isUserReported")
    }

    private fun handleUserSubscription(isSubscribed: Boolean) {
        if (userSubscription) {
            binding.tvSubs.text = getString(R.string._subscribed)
//            binding.tvNotified.visibility = GONE
//            binding.tvMessage.visibility = VISIBLE
        } else {
            binding.tvSubs.text = getString(R.string._subscribe)
//            binding.tvMessage.visibility = GONE
//            binding.tvNotified.visibility = VISIBLE
        }
        userSubscription = !userSubscription
    }

    private fun showOtherUserProfile(otherUserProfile: OtherUserProfile) {
        viewModel.checkTeacher(otherUserProfile.user.id)
        binding.apply {
            Analytics.logEvent(
                Analytics.Event.UserProfileViewEvent(
                    Analytics.Event.Param.UserId(otherUserProfile.user.id)
                )
            )
            userId = otherUserProfile.user.id
            userHandle = otherUserProfile.user.handle.checkUserHandle()
            tvHandle.text = userHandle
            tvDisplayName.text = otherUserProfile.user.name
            if (otherUserProfile.user.city == "" && otherUserProfile.user.state == "") {
                tvLocation.visibility = INVISIBLE
            } else if (otherUserProfile.user.city == "") {
                tvLocation.text = otherUserProfile.user.state
            } else if (otherUserProfile.user.state == "") {
                tvLocation.text = otherUserProfile.user.city
            } else {
                tvLocation.text = "${otherUserProfile.user.city}, ${otherUserProfile.user.state}"
            }
            tvFollwingNumber.apply {
                visibility = INVISIBLE
                text = prettyViewsCount(otherUserProfile.stats.subscribersCount.toLong())
            }
            tvFollwersNumber.apply {
                visibility = INVISIBLE
                text = prettyViewsCount(otherUserProfile.stats.subscription.toLong())
            }
            cvProfilePic.loadCenterCropImageFromUrl(otherUserProfile.user.dpUrl)
            tvLevelScore.text = "LVL ${otherUserProfile.stats.level}"
//            tvNotified.text = "Get notified each time \n${otherUserProfile.user.name} uploads a new cover"
            userSubscription = otherUserProfile.isSubscribed
            tvUserStatus.text = otherUserProfile.user.status
            if (userSubscription)
                binding.tvSubs.text = getString(R.string._subscribed)
            else
                binding.tvSubs.text = getString(R.string._subscribe)
            binding.tvSubs.isChecked = userSubscription
//            binding.tvMessage.visibility = if (userSubscription) VISIBLE else GONE
//            binding.tvNotified.visibility = if (userSubscription) GONE else VISIBLE

            userSubscription = !otherUserProfile.isSubscribed
            tvSubs.setOnClickListener {
                if (userSubscription) {
                    viewModel.subscribeUser(args.userId)
                    Analytics.logEvent(
                        Analytics.Event.SubscribeUserEvent(
                            Analytics.Event.Param.UserId(userId)
                        )
                    )
                } else {
                    viewModel.deleteSubscribeUser(args.userId)
                }
                binding.tvSubs.isChecked = userSubscription
//                binding.tvMessage.visibility = if (userSubscription) VISIBLE else GONE
//                binding.tvNotified.visibility = if (userSubscription) GONE else VISIBLE
            }

            tvMsg.setOnClickListener {
                val individualConversationIntent = Intent(context, ConversationActivity::class.java)
                individualConversationIntent.putExtra(ConversationUIService.USER_ID, otherUserProfile.user.id)
                individualConversationIntent.putExtra(ConversationUIService.DISPLAY_NAME, otherUserProfile.user.name)
                individualConversationIntent.putExtra(ConversationUIService.TAKE_ORDER, true)
                startActivity(individualConversationIntent)
            }
        }
    }

    private fun shareProfile() {
        Analytics.logEvent(
            Analytics.Event.ShareUserProfileEvent(
                Analytics.Event.Param.UserId(args.userId),
            )
        )
        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse("https://www.singshala.com/app/users/$userId")
            domainUriPrefix = "https://singshala.page.link"
            androidParameters("com.lucidmusic.singstr") {
            }
//            iosParameters("com.example.ios") {
//                appStoreId = "123456789"
//                minimumVersion = "1.0.1"
//            }
        }.addOnSuccessListener { (shortLink, flowchartLink) ->
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "$shortLink")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Share Profile.."))
        }
    }

    private fun showReportUser() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_report_cover, null)
        val mBuilder = AlertDialog.Builder(activity).setView(dialogView)
        dialogView.findViewById<TextView>(R.id.tvReportText).setText(R.string.report_user_reason_text)
        dialogView.findViewById<TextView>(R.id.btnReportFeed).setText(getString(R.string.report_user))

        val selectedStrings = ArrayList<String>()
        val alertDialog = mBuilder.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<TextView>(R.id.btnReportFeed).setOnClickListener {
            if (dialogView.findViewById<CheckBox>(R.id.cbAbusive).isChecked) selectedStrings.add("Abusive")
            if (dialogView.findViewById<CheckBox>(R.id.cbInappropriate).isChecked) selectedStrings.add("Inappropriate")
            if (dialogView.findViewById<CheckBox>(R.id.cbHateful).isChecked) selectedStrings.add("Hateful")
            if (dialogView.findViewById<CheckBox>(R.id.cbSpam).isChecked) selectedStrings.add("Spam")
            if (dialogView.findViewById<CheckBox>(R.id.cbFake).isChecked) selectedStrings.add("Fake Account")
            if (dialogView.findViewById<CheckBox>(R.id.cbHarrasment).isChecked) selectedStrings.add("Harrasment")
            if (dialogView.findViewById<CheckBox>(R.id.cbDisrespectful).isChecked) selectedStrings.add("Disrespectful")
            viewModel.reportUser(dialogView.findViewById<TextView>(R.id.tvReasonReport).text.toString(), selectedStrings, userId)
            alertDialog.dismiss()
        }
    }

    private fun handleIsTeacher(checkTeacher: CheckTeacher){
        if(checkTeacher.teacherId == "false"){
            binding.apply {
                tabTeacherVp.visibility = GONE
                vpTeacher.visibility = GONE
                rvProfile.visibility = VISIBLE
            }
            singstrViewModel.getUserSubscription()
        }else{
            binding.apply {
                tabTeacherVp.visibility = VISIBLE
                vpTeacher.visibility = VISIBLE
                rvProfile.visibility = GONE
                tvTeacherTag.visibility = VISIBLE
            }
            showTeacherDetails(checkTeacher.teacherId)
        }
        viewModel.loadOtherUserSubmits(args.userId)
    }

    private fun showTeacherDetails(teacherId: String) {
        teacherDetailsPagerAdapter = OtherTeacherDetailsPagerAdapter(this, teacherId, userId)
        binding.apply {
            vpTeacher.apply {
                adapter = teacherDetailsPagerAdapter
                isUserInputEnabled = false
                post {
                    vpTeacher.currentItem = mVpPosition
                }
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

    internal fun moveToUserFeedScreen(position: Int){
        findNavController().navigate(OtherUserProfileFragmentDirections.toUserFeedFragment(args.userId, position))
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

    internal fun moveToLessonListScreen(conceptId: String){
        mVpPosition = 0
        findNavController().navigate(OtherUserProfileFragmentDirections.toConceptLessonListFragment(conceptId))
    }

    internal fun moveToLessonScreen(lessonId: String){
        mVpPosition = 1
        findNavController().navigate(OtherUserProfileFragmentDirections.toLearnDetailFragment(lessonId))
    }
}