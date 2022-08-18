package com.sensibol.lucidmusic.singstr.network.framework

import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
internal class GetProfileWSTest : BaseWSTest() {

    @Test
    fun `getProfile fetch profile and maps to Profile`() {
        // given
        server.dispatcher = MockDispatcher
        val appWebServiceImpl = RetrofitAppWebService(getService())

        //when
        val result = runBlocking { appWebServiceImpl.getProfile() }

        // then
        assertEquals("Tony", result.firstName)
        assertEquals("Stark", result.lastName)
        assertEquals("Robert J", result.artistName)
        assertEquals("1010101010", result.mobileNumber)
        assertEquals("stark@tony.com", result.email)
        assertEquals(true, result.isEmailVerified)
    }
}