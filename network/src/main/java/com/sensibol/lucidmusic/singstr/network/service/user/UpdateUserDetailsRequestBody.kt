package com.sensibol.lucidmusic.singstr.network.service.user

import android.view.Display
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateUserDetailsRequestBody(
    val first_name: String = "",
    val last_name: String?,
    val contact_number: String?,
    val email: String?,
    val user_handle: String?,
    val display_name: String?,
    val bio: String?,
    val sex: String?,
    val dob: String?,
    val city: String?,
    val state: String?,
    val singerType: String?
)
