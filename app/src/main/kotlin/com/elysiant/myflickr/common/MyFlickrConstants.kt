package com.elysiant.myflickr.common

import com.elysiant.myflickr.util.DecimalByteUnit

/**
 * Created by vicsonvictor on 10/17/2020.
 */
object MyFlickrConstants {

    const val HTTP = "http"
    const val HTTP_TIMEOUT_VALUE = 10
    const val FULL_SCREEN_PHOTO_URL_EXTRA = "PhotoUrlExtra"
    const val LOADING_MORE_PHOTO_ID_PLACE_HOLDER = "FAKE_PHOTO_ID"

    // API Constants
    const val PHOTOS_PER_PAGE = 100
    const val PHOTOS_METHOD = "flickr.photos.search"
    const val API_KEY = "api_key"
    const val RESPONSE_FORMAT = "format"
    const val RESPONSE_FORMAT_VALUE = "json"
    const val NO_JSON_CALLBACK = "nojsoncallback"
    const val PHOTOS_EXTRAS = "url_s,url_c"

    // Disk cache size used for Picasso - set to 50MB for now.
    @JvmField
    val IMAGE_DISK_CACHE_SIZE = DecimalByteUnit.MEGABYTES.toBytes(50).toInt()

}
