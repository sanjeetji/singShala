package com.sensibol.lucidmusic.singstr.domain.model


data class NotificationsData(
    val status:Int,
    val message:String,
    val data:NotificationsList
)