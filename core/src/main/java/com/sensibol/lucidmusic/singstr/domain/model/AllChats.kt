package com.sensibol.lucidmusic.singstr.domain.model

data class AllChats(
    val messageKey: String,
    val userKey: String,
    val to: String,
    val contactIds: String,
    val message: String,
    val delivered: Boolean,
    val read: Boolean,
    val sent: Boolean,
    val pairedMessageKey: String,
)
