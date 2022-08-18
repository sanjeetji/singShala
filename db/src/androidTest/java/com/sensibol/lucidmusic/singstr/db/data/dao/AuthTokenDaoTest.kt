package com.sensibol.lucidmusic.singstr.db.data.dao

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AuthTokenDaoTest {

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppRoomDatabase::class.java).build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        (db as RoomDatabase).close()
    }

    @Test(expected = NullPointerException::class)
    fun reading_auth_token_from_empty_database_returns_null() = runBlocking {
        val cv = db.getAuthToken()
    }

    @Test
    fun auth_token_read_should_be_equal_to_the_added_one() = runBlocking {
        val authToken: AuthToken = AuthToken("accessToken", "tokenType", "expiryTime")
        db.setAuthToken(authToken)

        assertEquals(authToken.accessToken, db.getAuthToken()?.accessToken)
    }
}