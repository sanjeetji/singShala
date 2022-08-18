package com.sensibol.lucidmusic.singstr.domain.model

object AuthTokenFixture {

    fun newAuthToken(
        accessToken: String = "accessToken",
        tokenType: String = "tokenType",
        expiryTime: String = "expiryTime",
    ): AuthToken =
        AuthToken(
            accessToken = accessToken,
            tokenType = tokenType,
            expiryTime = expiryTime
        )
}