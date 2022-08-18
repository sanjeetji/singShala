package com.sensibol.lucidmusic.singstr.gui.app.notifications

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.Notifications
import com.sensibol.lucidmusic.singstr.domain.model.NotificationsList
import com.sensibol.lucidmusic.singstr.gui.app.explore.ExploreSongView
import com.sensibol.lucidmusic.singstr.gui.app.util.loadCenterCropImageFromUrl
import com.sensibol.lucidmusic.singstr.gui.convertDatePatternComments
import com.sensibol.lucidmusic.singstr.gui.databinding.TileNotificationBinding
import javax.inject.Inject
import kotlin.properties.Delegates

class NotificationAdapter @Inject constructor() : RecyclerView.Adapter<NotificationAdapter.NotificationVH>() {

    internal var onNotificationListener: (Notifications,Int) -> Unit = { notifications,position ->  }

    internal var notifications: List<Notifications> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NotificationVH(
        TileNotificationBinding.inflate(parent.layoutInflater, parent, false)
    )

    override fun onBindViewHolder(holder: NotificationVH, position: Int) = holder.bind(notifications[position])

    override fun getItemCount(): Int = notifications.size

    fun addToNotification(notificationsList: List<Notifications>) {
        notifications = if(notifications.isEmpty()){
            notificationsList
        }else{
            val result:MutableList<Notifications> = mutableListOf()
            result.addAll(notifications)
            result.addAll(notificationsList)
            result
        }
        notifyDataSetChanged()
    }


    inner class NotificationVH(private val binding: TileNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notifications: Notifications) {
            binding.apply {
//                val notifications = this@NotificationAdapter.notifications[adapterPosition]
                if (notifications.thumbnailUrl.isNotEmpty())
                    cvProfilePic.loadCenterCropImageFromUrl(notifications.thumbnailUrl)
                tvNotificationText.text = notifications.message
                tvNotificationTime.text = convertDatePatternComments(notifications.timestamp)
                clNotifications.setOnClickListener {
                    onNotificationListener(notifications,position)
                }
            }
        }
    }
}