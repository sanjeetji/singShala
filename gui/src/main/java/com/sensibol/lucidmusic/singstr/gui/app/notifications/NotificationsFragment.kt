package com.sensibol.lucidmusic.singstr.gui.app.notifications

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.NotificationsList
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentNotificationsBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsFragment : BaseFragment() {

    override val layoutResId = R.layout.fragment_notifications
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentNotificationsBinding::inflate
    override val binding: FragmentNotificationsBinding get() = super.binding as FragmentNotificationsBinding

    private val viewModel: NotificationsViewModel by viewModels()

    @Inject
    lateinit var notificationAdapter: NotificationAdapter

    override fun onInitView() {

        Analytics.logEvent(
            Analytics.Event.NotificationPageViewEvent(
                Analytics.Event.Param.ScrollPercent("NA"),
            )
        )
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(notifications,:: showAllNotifications)
            loadNotifications()
        }

        binding.apply {
            rvNotification.apply {
                layoutManager = LinearLayoutManager(root.context)
                adapter = notificationAdapter
            }

            notificationAdapter.onNotificationListener = { notifications,postion ->
                Analytics.logEvent(
                    Analytics.Event.NotificationClickEvent(
                        Analytics.Event.Param.NotificationCategory(notifications.message)
                    )
                )
                try {
                    findNavController().navigate(Uri.parse(notifications.deepLinks))
                } catch (e: Exception) {
                    findNavController().navigate(R.id.homeFragment)
                }
            }

            rvNotification.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        Timber.d("Need to load more data")
                        binding.pbLoading.visibility = View.VISIBLE
                        viewModel.loadNotifications()
                    }
                }
            })

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun showAllNotifications(notificationsList: NotificationsList) {
        binding.pbLoading.visibility = View.GONE
        notificationAdapter.addToNotification(notificationsList.notifications)
    }
}