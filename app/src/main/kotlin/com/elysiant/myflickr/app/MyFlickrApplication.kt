package com.elysiant.myflickr.app

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.LruCache
import androidx.core.app.JobIntentService
import com.elysiant.myflickr.BuildConfig
import com.elysiant.myflickr.app.injection.component.DaggerMyFlickrComponent
import com.elysiant.myflickr.app.injection.component.MyFlickrComponent
import com.elysiant.myflickr.app.injection.module.MyFlickrModule
import com.elysiant.myflickr.common.log.DebugTree
import com.elysiant.myflickr.service.MyFlickrStartupIntentService
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by vicsonvictor on 10/17/2020.
 */
class MyFlickrApplication : Application() {

    val JOB_ID = 12345

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

    override fun onCreate() {
        super.onCreate()
        initiateAppStartupSequence()
    }

    fun component(): MyFlickrComponent {
        return component
    }

    private fun setupTimber() {
        when (BuildConfig.DEBUG) {
            true -> Timber.plant(DebugTree(TAG))
            false -> Timber.plant(DebugTree(TAG))    // TODO - VV - Also Set up Crashlytics tree as needed for release version.
        }
    }

    /**
     * Triggers the [MyFlickrStartupIntentService] to initiate the app
     * start up data fetch sequence.
     */
    private fun initiateAppStartupSequence() {

        // Start start-up intent service, if not already running
        val manager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var startUpServiceRunning = false
        for (service in manager.runningAppProcesses) {
            if ("com.elysiant.myflickr.service.MyFlickrStartupIntentService" == service.processName) {
                Timber.d("MyFlickrStartupIntentService is already running")
                startUpServiceRunning = true
            }
        }

        if (!startUpServiceRunning) {
            Timber.i("Starting MyFlickrStartupIntentService ....")
            JobIntentService.enqueueWork(baseContext, MyFlickrStartupIntentService::class.java, JOB_ID, Intent())
        }

    }

    companion object {
        private val TAG = MyFlickrApplication::class.java.simpleName
    }

}
