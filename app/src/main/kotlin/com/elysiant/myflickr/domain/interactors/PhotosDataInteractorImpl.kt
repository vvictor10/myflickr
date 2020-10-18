package com.elysiant.myflickr.domain.interactors

import com.elysiant.myflickr.data.service.FlickrApi
import com.elysiant.myflickr.data.service.room.FlickrApiLocalStorage
import com.elysiant.myflickr.models.PhotosResponse
import com.elysiant.myflickr.util.NetworkListener
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.adapter.rxjava2.Result
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Acts as a facade between the data service layer and the UI Layer.
 */
@Singleton
class PhotosDataInteractorImpl @Inject
constructor(private val flickrApi: FlickrApi, private val flickrLocalStorage: FlickrApiLocalStorage) :
    PhotosDataInteractor {

    /**
     * Used to search for photos based on a search string.
     */
    override fun searchForPhotos(queryTerm: String, pageNo: Int): Observable<Result<PhotosResponse>> {
        return if (NetworkListener.isOnline) {
            flickrApi.searchPhotos(queryTerm, pageNo)
                .flatMap { photosResultResponse ->
                    // Save to local storage
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            photosResultResponse.response()?.body()?.photos?.photosList?.let {
                                Timber.d("No. of new photos to add to Room: %s", it.size)
                                flickrLocalStorage.savePhotos(it)
                            }
                        } catch (e: Exception) {
                            Timber.w(e, "Unexpected error when saving photos to Room Local Storage")
                        }
                    }
                    Observable.just(photosResultResponse)
                }
        } else {
            flickrLocalStorage.searchPhotos(queryTerm, pageNo)
        }
    }

}
