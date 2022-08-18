package com.sensibol.lucidmusic.singstr.domain.model

data class MessagingUser(
    val message: String,
    val userKey: String,
    val deviceKey: String,
    val userId: String,
    val displayName: String,
    val deactivate: Boolean,
    val userEncryptionKey: String,
    val totalUnreadCount: Int,
    val roleType: Int,
    val notifyState: Int,
    val newUser: Boolean,
    val authToken: String
)
