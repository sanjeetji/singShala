package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class FollowingUser(
    val _id: String,
    val email: String,
    val first_name: String,
    val last_name: String,
    val user_handle: String,
    val image: String,
    val subscribe: Boolean
)