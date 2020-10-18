package com.elysiant.myflickr.app

import android.app.Application
import android.content.Context
import android.util.LruCache
import com.elysiant.myflickr.BuildConfig
import com.elysiant.myflickr.app.injection.component.DaggerMyFlickrComponent
import com.elysiant.myflickr.app.injection.component.MyFlickrComponent
import com.elysiant.myflickr.app.injection.module.MyFlickrModule
import com.elysiant.myflickr.common.log.DebugTree
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by vicsonvictor on 10/17/2020.
 */
class MyFlickrApplication : Application() {

    @Inject
    lateinit var lruCache: LruCache<Any, Any>

    private val component: MyFlickrComponent by lazy {
        DaggerMyFlickrComponent.builder().myFlickrModule(MyFlickrModule(this)).build()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        component.inject(this)
        setupTimber()
    }

    fun component(): MyFlickrComponent {
        return component
    }

    private fun setupTimber() {
        when (BuildConfig.DEBUG) {
            true -> Timber.plant(DebugTree(TAG))
            false -> Timber.plant(DebugTree(TAG))  // TODO - VV - Set up Crashlytics tree as needed for release version.
        }
    }

    companion object {
        private val TAG = MyFlickrApplication::class.java.simpleName
    }

}
