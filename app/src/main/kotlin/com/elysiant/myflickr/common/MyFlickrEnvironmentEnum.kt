package com.elysiant.myflickr.common

enum class MyFlickrEnvironmentEnum private constructor(var flickrApiBaseUrl: String) {

    PROD("https://api.flickr.com/services/"),
    STAGING("https://api.flickr.com/services/")
}
