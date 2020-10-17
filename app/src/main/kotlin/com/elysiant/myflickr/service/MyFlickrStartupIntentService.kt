package com.elysiant.myflickr.service

import android.content.Intent
import android.util.LruCache
import androidx.core.app.JobIntentService
import com.elysiant.myflickr.app.MyFlickrApplication
import com.elysiant.myflickr.domain.interactors.MyFlickrStartupDataInteractor
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by vicsonvictor on 10/17/2020.
 */

class MyFlickrStartupIntentService : JobIntentService() {

    @Inject
    lateinit var myFlickrStartupDataInteractor: MyFlickrStartupDataInteractor

//    @Inject
//    lateinit var preferenceManager: PlacesSearchPreferenceManager

    @Inject
    lateinit var lruCache: LruCache<Any, Any>

    override fun onHandleWork(intent: Intent) {
        Timber.d("Handling intent..")
        myFlickrStartupDataInteractor.fetchAndCacheData()
    }

    override fun onCreate() {
        super.onCreate()
        (application as MyFlickrApplication).component().inject(this)
    }

}
