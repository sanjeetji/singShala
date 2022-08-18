package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.Notifications
import com.sensibol.lucidmusic.singstr.domain.model.NotificationsData
import com.sensibol.lucidmusic.singstr.domain.model.NotificationsList
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/*internal fun GetNotificationsResponse.Notifications.toNotifications() = NotificationsList(
    notifications = notificationData.map { it.toNotifications() }
)

internal fun GetNotificationsResponse.Notifications.Data.toNotifications() = Notifications(
    id = id,
    message = message,
    thumbnailUrl = thumbnailUrl,
    timestamp = timestamp,
    deepLinks = deepLink,
)*/

internal fun GetNotificationsResponse.toNotificationsData() = NotificationsData(
    status = status,
    message = message,
    data = data.toNotificationsList()
//    data = data.map { it.toNotificationsList() }
)

internal fun GetNotificationsResponse.Notifications.toNotificationsList() = NotificationsList(
    notifications = notificationData.map { it.toNotifications() },
    nextPageToken = nextPageToken
)

internal fun GetNotificationsResponse.Notifications.Data.toNotifications() = Notifications(
    id = id,
    message = message,
    thumbnailUrl = thumbnailUrl,
    timestamp = timestamp,
    deepLinks = deepLink,
)

@JsonClass(generateAdapter = true)
data class GetNotificationsResponse(
    @Json(name = "status")
    val status:Int,
    @Json(name = "message")
    val message: String,
    @Json(name = "data")
    val data: Notifications
) {
    @JsonClass(generateAdapter = true)
    data class Notifications(
        @Json(name = "data")
        val notificationData: List<Data>,
        @Json(name = "next_page_token")
        val nextPageToken:String
    ) {
        @JsonClass(generateAdapter = true)
        data class Data(
            @Json(name = "notification_id")
            val id: String,
            @Json(name = "notification_event_type")
            val event_type: String,
            @Json(name = "notification_type_image_url")
            val thumbnailUrl: String,
            @Json(name = "message")
            val message: String,
            @Json(name = "title")
            val title: String,
            @Json(name = "time")
            val timestamp: String,
            @Json(name = "seen")
            val seen: Boolean,
            @Json(name = "deep_link")
            val deepLink: String
        )
    }
}