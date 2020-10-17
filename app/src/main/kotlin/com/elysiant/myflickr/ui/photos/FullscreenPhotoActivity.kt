package com.elysiant.myflickr.ui.photos

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.drawerlayout.widget.DrawerLayout
import com.elysiant.myflickr.R
import com.elysiant.myflickr.common.MyFlickrConstants
import com.elysiant.myflickr.ui.common.BaseNavigationActivity
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import javax.inject.Inject

class FullscreenPhotoActivity : BaseNavigationActivity() {

    @Inject
    lateinit var picasso: Picasso

    private lateinit var fullScreenImageView: ImageView

    private var isFullscreen: Boolean = false

    override fun getNavigationView(): NavigationView? {
        return null
    }

    override fun getDrawerLayout(): DrawerLayout? {
        return null
    }

    @SuppressLint("ClickableViewAccessibility", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fullscreen_photo)
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)

        component().inject(this)

        isFullscreen = true

        // Set up the user interaction to manually show or hide the system UI.
        fullScreenImageView = findViewById(R.id.full_screen_photo_image)

        val imageUrl = intent.getStringExtra(MyFlickrConstants.FULL_SCREEN_PHOTO_URL_EXTRA)

        if (imageUrl == null) {
            fullScreenImageView.setImageDrawable(getDrawable(android.R.drawable.stat_notify_error))
        } else {
            picasso.load(imageUrl).into(fullScreenImageView)
        }
    }

}