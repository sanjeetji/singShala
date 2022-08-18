package com.sensibol.lucidmusic.singstr.network.framework

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
internal class GetSongsWSTest : BaseWSTest() {

    @Test
    fun `getSongs fetches list of songs and maps to Songs`() {
        // given
        server.dispatcher = MockDispatcher
        val appWebServiceImpl = RetrofitAppWebService(getService())

        // when
        val result = runBlocking { appWebServiceImpl.getSongs() }

        // then
        assertEquals("Song Title 01", result[0].title)
        assertEquals("song_id_02", result[1].id)
        assertEquals(2021, result[0].year)
        assertEquals(true, result[1].isPremium)
        assertEquals("Rock", result[0].genres[0])
        assertEquals("artist_id_02", result[1].artists[0].id)
    }

}