package com.sensibol.lucidmusic.singstr.network.service.user

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateUserOnboardStatusRequestBody(
    val status: Boolean
)
