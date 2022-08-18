package com.sensibol.lucidmusic.singstr.domain.model

data class User(
    override val id: String,
    override val handle: String,
    override val dpUrl: String,
    val name: String,
    val lastName: String = "",
    val mobileNumber: String,
    val email: String,
    val isVerified: Boolean,
    val status: String,
    val dob: String,
    val city: String,
    val state: String,
    val sex: String,
    val isOnBoarded: Boolean
) : UserMini(id, handle, dpUrl)
