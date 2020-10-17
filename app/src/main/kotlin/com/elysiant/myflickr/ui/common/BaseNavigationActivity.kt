package com.elysiant.myflickr.ui.common

import android.content.Context
import android.content.Intent
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.elysiant.myflickr.BuildConfig
import com.elysiant.myflickr.R
import com.elysiant.myflickr.app.MyFlickrApplication
import com.elysiant.myflickr.common.MyFlickrConstants
import com.elysiant.myflickr.models.PhotoItem
import com.elysiant.myflickr.ui.component.ActivityComponent
import com.elysiant.myflickr.ui.component.DaggerActivityComponent
import com.elysiant.myflickr.ui.photos.FullscreenPhotoActivity
import com.elysiant.myflickr.util.NetworkListener
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.custom_toast.view.*
import kotlinx.android.synthetic.main.search_toolbar.*

/**
 * All concrete activities must extend this class to get injection and other goodies for free.
 */
abstract class BaseNavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var networkListener: NetworkListener = NetworkListener()

    private val activityComponent: ActivityComponent by lazy {
        DaggerActivityComponent.builder()
                .myFlickrComponent((application as MyFlickrApplication).component())
                .build()
    }
    override fun onResume() {
        super.onResume()
        networkListener.registerNetworkListener(this)
    }

    override fun onPause() {
        super.onPause()
        networkListener.unregisterNetworkListener()
    }

    override fun onBackPressed() {

        if (getDrawerLayout() == null) {
            super.onBackPressed()
        } else {
            when (getDrawerLayout()?.isDrawerOpen(GravityCompat.START)) {
                true -> getDrawerLayout()?.closeDrawer(GravityCompat.START)
                false -> super.onBackPressed()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home, R.id.nav_settings, R.id.nav_favorites, R.id.nav_explore -> {
                getDrawerLayout()?.openDrawer(GravityCompat.START)  // OPEN DRAWER
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        getDrawerLayout()?.closeDrawer(GravityCompat.START)
        when (item.itemId) {
            R.id.nav_favorites, R.id.nav_explore, R.id.nav_settings -> {
                displaySnackBarMessage("Under construction. Coming soon!")
                return false
            }
        }
        return true
    }

    open fun component(): ActivityComponent {
        return activityComponent
    }

    protected fun setupDrawerListeners() {
        val toggle = ActionBarDrawerToggle(
                this, getDrawerLayout(), toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        getDrawerLayout()?.addDrawerListener(toggle)
        getDrawerLayout()?.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                hideKeyboard()
            }

            override fun onDrawerOpened(drawerView: View) {
                hideKeyboard()
            }

            override fun onDrawerClosed(drawerView: View) {
                hideKeyboard()
            }

            override fun onDrawerStateChanged(newState: Int) {
                hideKeyboard()
            }
        })

        toggle.syncState()
    }

    protected open fun initToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(false)
        actionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationIcon(R.drawable.hamburger)
    }

    protected fun showKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val focusedView = currentFocus
        if (focusedView != null) {
            imm.toggleSoftInputFromWindow(this.currentFocus?.windowToken,
                    InputMethodManager.SHOW_FORCED, 0)
        }
    }

    protected fun setupNavigationView() {
        val menu = getNavigationView()?.menu
        if (menu?.getItem(1) != null) {
            menu.getItem(1).isChecked = true
        }
    }

    protected fun hideKeyboard() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        val focusedView = currentFocus
        if (focusedView != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
        }
    }

    fun displaySnackBarMessage(message: String) {
        val snackbar = Snackbar.make(window.decorView.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val tv = snackbar.view.findViewById<TextView>(R.id.snackbar_text)
        tv.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        snackbar.show()
    }

    protected fun goToFullScreenPhotoActivity(photo: PhotoItem) {
        val intent = Intent(applicationContext, FullscreenPhotoActivity::class.java)
        intent.putExtra(MyFlickrConstants.FULL_SCREEN_PHOTO_URL_EXTRA, photo.largeUrl ?: photo.smallUrl)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
    }

    abstract fun getNavigationView(): NavigationView?

    abstract fun getDrawerLayout(): DrawerLayout?

}