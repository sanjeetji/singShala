package com.sensibol.lucidmusic.singstr.gui.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavArgument
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.viewbinding.ViewBinding
import com.applozic.mobicomkit.Applozic
import com.applozic.mobicomkit.api.account.register.RegistrationResponse
import com.applozic.mobicomkit.listners.AlPushNotificationHandler
import com.google.firebase.messaging.FirebaseMessaging
import com.sensibol.android.base.gui.activity.BaseActivity
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.SingMode
import com.sensibol.lucidmusic.singstr.domain.model.User
import com.sensibol.lucidmusic.singstr.gui.*
import com.sensibol.lucidmusic.singstr.gui.app.sing.prepare.PrepareSingHostFragmentArgs
import com.sensibol.lucidmusic.singstr.gui.app.util.loadCenterCropImageFromUrl
import com.sensibol.lucidmusic.singstr.gui.databinding.ActivitySingstrBinding
import com.sensibol.lucidmusic.singstr.gui.app.util.ProgressBarAnimation
import com.webengage.sdk.android.WebEngage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
internal class SingstrActivity : BaseActivity() {

    override val layoutResId = R.layout.activity_singstr
    override val bindingInflater: (LayoutInflater) -> ViewBinding = ActivitySingstrBinding::inflate
    override val binding: ActivitySingstrBinding get() = super.binding as ActivitySingstrBinding

    private val viewModel: SingstrViewModel by viewModels()

    companion object {
        const val fcm_token_service = "com.sensibol.lucidmusic.singstr"
        const val messaging_user_id = "com.sensibol.lucidmusic.singstr.app.Messaging_User"
    }

    // FIXME - to be refactored
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val token = intent?.getStringExtra("new_fcm_token")
            viewModel.apply {
                setFCMToken(token)
                if (token != null) {
                    subscribeNotification(token)
                }
            }
        }
    }

    private fun showUserProfile(user: User) {
        binding.apply {
            binding.bnvAppBottomNav.loadImage(
                user.dpUrl, R.id.profileFragment, R.drawable.ic_profile, R.id.profileFragment
            )
        }
    }


    private val mBluetoothReceiver = BTReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.ivStageRibbon.visibility = if ("stage" == BuildConfig.FLAVOR) VISIBLE else GONE
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                Timber.e("Couldn't read firebase messaging toke. ${it.exception}")
            } else {
                Timber.d("FirebaseMessagingToken: ${it.result}")
                WebEngage.get().setRegistrationID(it.result)
                viewModel.setFCMToken(it.result)
                Applozic.registerForPushNotification(
                    this@SingstrActivity,
                    it.result,
                    object : AlPushNotificationHandler {
                        override fun onSuccess(registrationResponse: RegistrationResponse?) {
                            Timber.d("Token updated to applozic server")
                        }

                        override fun onFailure(
                            registrationResponse: RegistrationResponse?,
                            exception: java.lang.Exception?
                        ) {
                        }
                    })
            }
        }

        (supportFragmentManager.findFragmentById(R.id.navHostFragmentSingstr) as NavHostFragment).navController.apply {
            binding.bnvAppBottomNav.setupWithNavController(this)
            addOnDestinationChangedListener { _, destination, _ ->
                binding.bnvAppBottomNav.visibility = when (destination.id) {
                    R.id.onBoardNameFragment,
                    R.id.onBoardDetailsFragment,
                    R.id.onBoardGenreFragment,
                    R.id.onBoardLanguageFragment,
                    R.id.onBoardSongRecommendationFragment,
                    R.id.coverCommentsFragment,
                    R.id.coverPublishFragment,
                    R.id.practiceResultFrag,
                    R.id.feedFragment,
                    R.id.coverFeedFragment,
                    R.id.genericFeedFragment,
                    R.id.songFeedFragment,
                    R.id.userFeedFragment,
                    R.id.prepareSingHostFragment,
                    R.id.coverResultFragment,
                    R.id.scoreCardFragment,
                    R.id.coverProcessFragment,
                    R.id.coverPreviewFragment,
                    R.id.analysisVsPublishChoiceFragment,
                    R.id.detailAnalysisFragment,
                    R.id.settingsFragment,
                    R.id.exerciseLauncherFrag,
                    R.id.exerciseResultFrag,
                    R.id.editProfileFragment,
                    R.id.appWalkThroughFragment,
                    R.id.coverThumbnailSelectionFragment,
                    R.id.searchFragment,
                    R.id.exclusiveFeaturesFragment,
                    R.id.claimFreeSubscriptionFragment,
                    R.id.conceptLessonListFragment,
                    R.id.lessonListFragment,
                    R.id.followingFollowersPagerFragment,
                    R.id.otherUserProfileFragment,
                    R.id.notificationsFragment,
                    R.id.notificationSettingFragment,
                    R.id.coversDraftFragment,
                    R.id.dobGenderFragment,
                    R.id.nameFragment,
                    R.id.pickUsernameFragment,
                    R.id.singerLevelFragment -> GONE
                    else -> VISIBLE
                }
            }
        }

        window.statusBarColor = this.let { ContextCompat.getColor(it, R.color.bg_page) }
        viewModel.apply {
            observe(setFCMToken, ::storeFCMToken)
            observe(isSubscribed, ::isNotificationSubscribed)
            observe(user, ::showUserProfile)
            loadUserProfile()
        }

    }

    private fun isNotificationSubscribed(b: Boolean) = Unit

    private fun storeFCMToken(isTokenSaved: Boolean) {
        Timber.d("storeFCMToken: $isTokenSaved")
    }

    fun selectBottomMenu(menuId: Int) {
        binding.bnvAppBottomNav.menu.findItem(menuId).isChecked = true
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(mReceiver, IntentFilter(fcm_token_service))
    }

//    override fun onResume() {
//        super.onResume()
//        registerReceiver(mBluetoothReceiver, IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED))
//    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(mReceiver)
//        unregisterReceiver(mBluetoothReceiver)
    }
}
