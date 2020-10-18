package com.elysiant.myflickr.util

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.ViewFinder
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher
import java.lang.reflect.Field

/**
 * This IdlingResource can be used in UI tests to wait for states of view elements to be updated
 * based on data updates etc.
 *
 * @param viewMatcher The matcher to find the view.
 * @param idleMatcher The matcher condition to be fulfilled to be considered idle.
 */
class ViewIdlingResource(
        private val viewMatcher: Matcher<View?>?,
        private val idleMatcher: Matcher<View?>?
) : IdlingResource {

    private var resourceCallback: IdlingResource.ResourceCallback? = null

    /**
     * {@inheritDoc}
     */
    override fun isIdleNow(): Boolean {
        val view: View? = getView(viewMatcher)
        val isIdle: Boolean = idleMatcher?.matches(view) ?: false
        if (isIdle) {
            resourceCallback?.onTransitionToIdle()
        }
        return isIdle
    }

    /**
     * {@inheritDoc}
     */
    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback?) {
        this.resourceCallback = resourceCallback
    }

    /**
     * {@inheritDoc}
     */
    override fun getName(): String? {
        return "$this ${viewMatcher.toString()}"
    }

    /**
     * Tries to find the view associated with the given [<].
     */
    private fun getView(viewMatcher: Matcher<View?>?): View? {
        return try {
            val viewInteraction = Espresso.onView(viewMatcher)
            val finderField: Field? = viewInteraction.javaClass.getDeclaredField("viewFinder")
            finderField?.isAccessible = true
            val finder = finderField?.get(viewInteraction) as ViewFinder
            finder.view
        } catch (e: Exception) {
            null
        }
    }

}

/**
 * Waits for a matching View or throws an error if it's taking too long.
 */
fun waitUntilViewIsDisplayed(matcher: Matcher<View?>) {
    val idlingResource: IdlingResource = ViewIdlingResource(matcher, ViewMatchers.isDisplayed())
    try {
        IdlingRegistry.getInstance().register(idlingResource)
        // First call to onView is to trigger the idler.
        Espresso.onView(ViewMatchers.withId(0)).check(ViewAssertions.doesNotExist())
    } finally {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }
}