package com.sensibol.lucidmusic.singstr.gui.app.sing.result

import androidx.navigation.Navigation.setViewNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.sensibol.lucidmusic.singstr.domain.model.CoverResultFixture
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.sing.result.cover.CoverResultFragment
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
internal class SingScoreNavigationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun navigate_from_cover_result_to_cover_preview() {
        val coverResult = CoverResultFixture.newCoverResult()
        val fragmentArgs = CoverResultFragmentArgs(coverResult).toBundle()

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        runOnUiThread {
            navController.setGraph(R.navigation.sing)
            navController.setCurrentDestination(R.id.coverResultFragment, fragmentArgs)
        }

        launchFragmentInHiltContainer<CoverResultFragment>(fragmentArgs) {
            setViewNavController(it.requireView(), navController)
        }

        assertEquals(R.id.coverResultFragment, navController.currentDestination?.id)

        onView(withId(R.id.btnSkip)).perform(click())
        onView(withId(R.id.btn_next)).perform(click())

        assertEquals(R.id.coverPreviewFragment, navController.currentDestination?.id)

    }

}