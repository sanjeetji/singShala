package com.sensibol.lucidmusic.singstr.network.data.response

import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
internal class GenerateAuthTokenResponseKtTest {

    @Test
    fun `AuthTokenResponse with full data maps to AuthToken domain model`() {
        // given
        val authTokenResponse = DataResponse(
            tokenResponse = TokenResponse("accessToken", "tokenType", "expiry"),
            isNewUser = true
        )

        val expected = AuthToken("accessToken", "tokenType", "expiry", true)

        // when
        val token = authTokenResponse.toAuthToken()

        // then
        assertEquals(expected, token)
    }
}