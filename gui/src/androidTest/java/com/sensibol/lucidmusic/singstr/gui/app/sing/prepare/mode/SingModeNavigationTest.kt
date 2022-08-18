package com.sensibol.lucidmusic.singstr.gui.app.sing.prepare.mode

import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import androidx.navigation.Navigation.setViewNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.sensibol.android.base.md5
import com.sensibol.lucidmusic.singstr.domain.AppFileSystem
import com.sensibol.lucidmusic.singstr.domain.FakeContentWebServiceModule
import com.sensibol.lucidmusic.singstr.domain.model.ContentUrls
import com.sensibol.lucidmusic.singstr.domain.model.ContentUrlsFixture
import com.sensibol.lucidmusic.singstr.domain.model.Song
import com.sensibol.lucidmusic.singstr.domain.model.SongFixture
import com.sensibol.lucidmusic.singstr.domain.webservice.ContentWebService
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.sing.prepare.PrepareSingHostFragment
import com.sensibol.lucidmusic.singstr.gui.app.sing.prepare.PrepareSingHostFragmentArgs
import com.sensibol.lucidmusic.singstr.gui.launchFragmentInHiltContainer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileWriter
import javax.inject.Inject
import javax.inject.Singleton

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
@UninstallModules(
    FakeContentWebServiceModule::class
)
internal class SingModeNavigationTest {

    @Module
    @InstallIn(SingletonComponent::class)
    inner class TestModule {

        @Provides
        @Singleton
        internal fun provideContentWebService(): ContentWebService =
            object : ContentWebService {

                override suspend fun getSongContentUrls(songId: String): ContentUrls =
                    ContentUrlsFixture.newContentUrls(mediaHash = mediaHash, metadataHash = metadataHash).also {
                        println("getSongContent: $it")
                    }
            }
    }

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var fs: AppFileSystem

    private lateinit var song: Song

    private lateinit var mediaFile: File
    private lateinit var mediaHash: String

    private lateinit var metadataFile: File
    private lateinit var metadataHash: String

    private fun mySetup() {
        song = SongFixture.newSong()

        mediaFile = fs.getMediaFile(song)
        println("JUnit: mySetup: $mediaFile")

        FileWriter(mediaFile).use { it.write("media") }
        mediaHash = runBlocking { (async { mediaFile.md5() }).await() }
        println("JUnit: mySetup: $mediaHash")

        metadataFile = fs.getMetadataFile(song)
        println("JUnit: mySetup: $metadataFile")

        FileWriter(metadataFile).use { it.write("metadata") }
        metadataHash = runBlocking { (async { metadataFile.md5() }).await() }
        println("JUnit: mySetup: $metadataHash")
    }


    @Before
    fun setUp() {
        hiltRule.inject()
        Intents.init()

        mySetup()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun singing_a_song_completely_shows_result_screen() {
        val fragmentArgs = PrepareSingHostFragmentArgs(song).toBundle()

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        runOnUiThread {
            navController.setGraph(R.navigation.sing, fragmentArgs)
        }

        launchFragmentInHiltContainer<PrepareSingHostFragment>(fragmentArgs) {
            setViewNavController(it.requireView(), navController)
        }

        assertEquals(R.id.prepareSingHostFragment, navController.currentDestination?.id)

        intending(anyIntent()).respondWith(
            Instrumentation.ActivityResult(RESULT_OK, SingIntentFixture.getSingSuccessResultIntent())
        )

        onView(withId(R.id.btn_record)).perform(click())

        assertEquals(R.id.coverResultFragment, navController.currentDestination?.id)


//
//        onView(withId(R.id.btnNext)).perform(click())
//
//        assertEquals(R.id.publishCoverFragment, navController.currentDestination?.id)
//
//        onView(withId(R.id.btnPublish)).perform(click())
//
//        assertEquals(R.id.publishCoverFragment, navController.currentDestination?.id)
//
//        onView(withId(R.id.btnPublish)).perform(click())
//
//        assertNull(navController.currentDestination)
    }
}
