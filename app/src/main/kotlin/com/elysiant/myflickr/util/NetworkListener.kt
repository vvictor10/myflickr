package com.elysiant.myflickr.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.elysiant.myflickr.R
import com.elysiant.myflickr.ui.common.BaseNavigationActivity
import com.github.pwittchen.reactivenetwork.library.ConnectivityStatus
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber

/**
 * Created by vicsonvictor on 10/17/2020.
 */
class NetworkListener {
    private var reactiveNetwork: Subscription? = null
    private var isOnline = true

    fun registerNetworkListener(context: Context?) {
        unregisterNetworkListener()
        reactiveNetwork = ReactiveNetwork().enableInternetCheck()
            .observeConnectivity(context)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { connectivityStatus ->
                isOnline =
                    if (connectivityStatus == ConnectivityStatus.OFFLINE || connectivityStatus == ConnectivityStatus.WIFI_CONNECTED_HAS_NO_INTERNET) {
                        Timber.i("Network connectivity lost")
                        if (context is BaseNavigationActivity) {
                            context.displaySnackBarMessage(context.resources.getString(R.string.connectivity_error))
                        }
                        false
                    } else {
                        true
                    }
            }
    }

    fun unregisterNetworkListener() {
        if (reactiveNetwork != null && !reactiveNetwork!!.isUnsubscribed) {
            reactiveNetwork!!.unsubscribe()
        }
    }

}