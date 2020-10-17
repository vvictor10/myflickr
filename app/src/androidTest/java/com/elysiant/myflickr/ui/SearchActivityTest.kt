package com.elysiant.myflickr.ui

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import com.elysiant.myflickr.R
import com.elysiant.myflickr.ui.photos.SearchActivity
import com.elysiant.myflickr.util.waitUntilViewIsDisplayed
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
@LargeTest
class SearchActivityTest {

    private val searchTerm = "dogs"

    @get:Rule
    val activityRule = ActivityScenarioRule(SearchActivity::class.java)

    @Before
    @Throws(Exception::class)
    fun setUp() {
        IdlingPolicies.setIdlingResourceTimeout(2, TimeUnit.SECONDS)
    }

    @Test
    fun testPhotosSearch() {

        // Touch the search toolbar to activate the keyboard
        Espresso.onView(ViewMatchers.withId(R.id.search_edittext))
                .perform(ViewActions.pressImeActionButton())

        // Type text and then press the button.
        Espresso.onView(ViewMatchers.withId(R.id.search_edittext))
                .perform(ViewActions.typeText(searchTerm), ViewActions.closeSoftKeyboard());

        // Check that the text was changed.
        Espresso.onView(ViewMatchers.withId(R.id.search_edittext))
                .check(ViewAssertions.matches(ViewMatchers.withText(searchTerm)));

        // Launch Search action
        Espresso.onView(ViewMatchers.withId(R.id.search_edittext)).perform(ViewActions.pressImeActionButton());

        // Wait until results recycler view appears
        waitUntilViewIsDisplayed(ViewMatchers.withId(R.id.search_results_recycler_view))

        // Check that results were displayed
        Espresso.onView(ViewMatchers.withId(R.id.search_results_recycler_view))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Check that loading indicator has disappeared
        Espresso.onView(ViewMatchers.withId(R.id.loading_indicator))
                .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))

    }

}