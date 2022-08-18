package com.sensibol.lucidmusic.singstr.domain.model

data class NotificationsList(
    val notifications: List<Notifications>,
    val nextPageToken:String
)

data class  Notifications(
    val id: String,
    val message: String,
    val thumbnailUrl: String,
    val timestamp: String,
    val deepLinks: String,
)