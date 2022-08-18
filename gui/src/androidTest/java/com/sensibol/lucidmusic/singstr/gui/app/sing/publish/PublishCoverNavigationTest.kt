package com.sensibol.lucidmusic.singstr.gui.app.sing.publish


import androidx.navigation.Navigation.setViewNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.sensibol.lucidmusic.singstr.domain.model.CoverResultFixture
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
internal class PublishCoverNavigationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun publish_cover() {
        val coverResult = CoverResultFixture.newCoverResult()
        val fragmentArgs = PublishCoverFragmentArgs(coverResult).toBundle()

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        runOnUiThread {
            navController.setGraph(R.navigation.sing)
            navController.setCurrentDestination(R.id.coverPublishFragment, fragmentArgs)
        }

        launchFragmentInHiltContainer<CoverPublishFragment>(fragmentArgs) {
            setViewNavController(it.requireView(), navController)
        }


        assertEquals(R.id.coverPublishFragment, navController.currentDestination?.id)

//        onView(withId(R.id.btnPublish)).perform(click())
//
//        assertEquals(R.id.publishCoverFragment, navController.currentDestination?.id)

    }
}