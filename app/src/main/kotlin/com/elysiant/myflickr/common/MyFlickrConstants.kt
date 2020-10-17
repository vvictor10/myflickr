package com.elysiant.myflickr.common

import com.elysiant.myflickr.util.DecimalByteUnit

/**
 * Created by vicsonvictor on 10/17/2020.
 */
object MyFlickrConstants {

    const val HTTP = "http"
    const val HTTP_TIMEOUT_VALUE = 10

    const val HEART_CROSS_FADE_ANIMATION_DURATION = 150

    // Disk cache size used for Picasso - set to 50MB for now.
    @JvmField
    val IMAGE_DISK_CACHE_SIZE = DecimalByteUnit.MEGABYTES.toBytes(50).toInt()

    const val CACHE_KEY_START_UP_PHOTOS = "startupCachedPhotos"

    const val FULL_SCREEN_PHOTO_URL_EXTRA = "PhotoUrlExtra"

    // API Constants

    const val PHOTOS_PER_PAGE = 200
    const val PHOTOS_METHOD = "flickr.photos.search"
    const val PHOTOS_EXTRAS = "url_s,url_c"
}
