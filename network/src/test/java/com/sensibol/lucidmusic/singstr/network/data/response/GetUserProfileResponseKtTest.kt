package com.sensibol.lucidmusic.singstr.network.data.response

import com.sensibol.lucidmusic.singstr.domain.model.User
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
internal class GetUserProfileResponseKtTest {

    @Test
    fun `ProfileResponse with full data maps to Profile domain model`() {
        //Given
        val profileResponse = ProfileResponse(
            firstName = "first Name",
            lastName = "last Name",
            artistName = "tony stark",
            mobileNumber = "9090909090",
            profilePicture = "www.profile-picture.com",
            email = "tony@email.com",
            isEmailVerified = true
        )

        val excepted = User(
            firstName = "first Name",
            lastName = "last Name",
            handle = "tony stark",
            mobileNumber = "9090909090",
            email = "tony@email.com",
            isVerified = false,
            dpUrl = "www.profile-picture.com",
            status = "",
        )

        //when
        val actual = profileResponse.toUser()

        //then
        assertEquals(excepted, actual)
    }
}