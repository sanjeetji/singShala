package com.sensibol.lucidmusic.singstr.domain.model

data class AuthToken(
    val accessToken: String,
    val tokenType: String,
    val expiryTime: String,
)