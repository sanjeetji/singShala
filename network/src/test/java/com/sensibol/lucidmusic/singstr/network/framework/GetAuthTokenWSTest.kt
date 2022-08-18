package com.sensibol.lucidmusic.singstr.network.framework

import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
internal class GetAuthTokenWSTest : BaseWSTest() {

    @Test
    fun `getAuthToken fetch authToken and maps to AuthToken`() {
        // given
        server.dispatcher = MockDispatcher
        val appWebServiceImpl = RetrofitAppWebService(getService())

        // When
        val result = runBlocking { appWebServiceImpl.getAuthToken() }

        // then
        assertEquals("token", result.token)
        assertEquals(true, result.isNewUser)
    }
}