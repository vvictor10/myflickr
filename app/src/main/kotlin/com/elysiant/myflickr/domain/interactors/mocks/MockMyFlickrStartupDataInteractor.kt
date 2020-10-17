package com.elysiant.myflickr.domain.interactors.mocks

import android.util.LruCache
import com.elysiant.myflickr.common.MyFlickrConstants
import com.elysiant.myflickr.data.service.local.FlickrApiLocal
import com.elysiant.myflickr.domain.interactors.MyFlickrStartupDataInteractor
import com.elysiant.myflickr.models.PhotoItem
import com.elysiant.myflickr.models.PhotosResponse
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.adapter.rxjava2.Result
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockMyFlickrStartupDataInteractor @Inject
constructor(private val lruCache: LruCache<Any, Any>, private val flickrApi: FlickrApiLocal) : MyFlickrStartupDataInteractor {

    private val disposables = CompositeDisposable()

    override fun fetchAndCacheData() {
        fetchTrendingPhotos()
    }

    private fun fetchTrendingPhotos() {
        @Suppress("UNCHECKED_CAST")
        val cached = lruCache.get(MyFlickrConstants.CACHE_KEY_START_UP_PHOTOS) as? List<PhotoItem>
        if (cached == null) {
            disposables.add(
                flickrApi.searchPhotos("dogs", 1)
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(TrendingPhotosSubscriber()))
        }
    }

    private inner class TrendingPhotosSubscriber() : DisposableObserver<Result<PhotosResponse>>() {

        override fun onComplete() {
            dispose()
        }

        override fun onError(e: Throwable) {
            Timber.e(e, "Unexpected error when fetching pictures during start up")
        }

        override fun onNext(result: Result<PhotosResponse>) {
            val photosResponse = result.response()?.body()
            if (photosResponse?.photos != null) {
                val photoItems = photosResponse.photos.photosList
                Timber.i("No. of Trending venues cached: %d", photoItems.size)
                lruCache.put(MyFlickrConstants.CACHE_KEY_START_UP_PHOTOS, photoItems)
            }
        }
    }


}