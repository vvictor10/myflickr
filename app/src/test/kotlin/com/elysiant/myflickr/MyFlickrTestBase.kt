package com.elysiant.myflickr


import android.util.LruCache
import com.elysiant.myflickr.data.service.local.FlickrApiTest
import com.elysiant.myflickr.domain.interactors.mocks.MockPhotosDataInteractor
import org.mockito.Mock
import java.io.File

abstract class MyFlickrTestBase {

    protected val flickrApi: FlickrApiTest
        get() = PLACES_API

    protected val photosDataInteractor = MockPhotosDataInteractor(flickrApi)

    @Mock
    protected lateinit var mockLruCache: LruCache<Any, Any>

    companion object {

        private const val RESOURCES_PATH = "/resources"
        private val PLACES_API = FlickrApiTest(resourcesPath)

        private val resourcesPath: String
            get() = File(".").absolutePath + RESOURCES_PATH
    }
}
