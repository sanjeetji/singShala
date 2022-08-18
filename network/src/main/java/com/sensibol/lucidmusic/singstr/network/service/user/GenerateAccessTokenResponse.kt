package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import com.squareup.moshi.JsonClass

internal fun GenerateAccessTokenResponse.Data.Token.toToken() = AuthToken(
    accessToken = access_token,
    tokenType = token_type,
    expiryTime = expiry,
)

@JsonClass(generateAdapter = true)
internal data class GenerateAccessTokenResponse(
    val success: Boolean,
    val data: Data
) {
    @JsonClass(generateAdapter = true)
    internal data class Data(
        val token: Token,
        val is_new_user: Boolean
    ) {
        @JsonClass(generateAdapter = true)
        internal data class Token(
            val access_token: String,
            val token_type: String,
            val expiry: String
        )
    }
}
