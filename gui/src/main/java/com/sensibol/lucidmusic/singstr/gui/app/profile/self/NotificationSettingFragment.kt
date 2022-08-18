package com.sensibol.lucidmusic.singstr.gui.app.profile.self

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.firebase.messaging.FirebaseMessaging
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentNotificationSettingBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class NotificationSettingFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_notification_setting
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentNotificationSettingBinding::inflate
    override val binding get() : FragmentNotificationSettingBinding = super.binding as FragmentNotificationSettingBinding

    private var isSubscribed = false
    private val viewModel: NotificationSettingViewModel by viewModels()


    override fun onInitView() {
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(isSubscribed, ::isNotificationSubscribed)
            observe(isUnsubscribed, ::isNotificationUnsubscribed)
        }

        binding.apply {
            cbNotification.setOnClickListener {
                FirebaseMessaging.getInstance().token.addOnCompleteListener {
                    if (!it.isSuccessful) {
                        Timber.e("Couldn't read firebase messaging token ${it.exception}")
                    } else {
                        if (cbNotification.isChecked) viewModel.subscribeNotification("${it.result}")
                        else viewModel.unsubscribeNotification("${it.result}")
                    }
                }

            }
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

        }
    }

    private fun isNotificationUnsubscribed(b: Boolean) = Unit

    private fun isNotificationSubscribed(b: Boolean) = Unit
}