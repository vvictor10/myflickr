package com.elysiant.myflickr.data.service.room

import com.elysiant.myflickr.data.localstorage.MyFlickrRoomDatabase
import com.elysiant.myflickr.data.service.FlickrApi
import com.elysiant.myflickr.models.PhotoItem
import com.elysiant.myflickr.models.Photos
import com.elysiant.myflickr.models.PhotosResponse
import io.reactivex.Observable
import kotlinx.coroutines.*
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlickrApiLocalStorage @Inject
constructor(private val roomDatabase: MyFlickrRoomDatabase) : FlickrApi {

    /**
     * TODO: Page number is not used right now.
     */
    override fun searchPhotos(searchTerm: String, pageNo: Int): Observable<Result<PhotosResponse>> {
        val photoItems: MutableList<PhotoItem> = ArrayList()
        runBlocking {
            photoItems.addAll(getPhotosFromLocalStorage(searchTerm))
        }
        val photos = Photos(photosList = photoItems, pageNo = 1, totalNoOfPages = 1)
        val response = PhotosResponse(photos = photos)
        return Observable.just(Result.response(Response.success(response)))
    }

    /**
     * TODO: Keeping it simple. The Room database will only store the latest fetch results from Flickr.
     */
    override fun savePhotos(photoItems: List<PhotoItem>) {
        CoroutineScope(Dispatchers.IO).launch {
            savePhotosToLocalStorage(photoItems)
        }
    }

    private suspend fun savePhotosToLocalStorage(photoItems: List<PhotoItem>) =
        withContext(Dispatchers.IO) {
            roomDatabase.myFlickrLocalDao().savePhotos(photoItems)
            Timber.i("No. of photo items saved to Room: %d", photoItems.size)
        }

    private suspend fun getPhotosFromLocalStorage(searchTerm: String): MutableList<PhotoItem> =
        withContext(Dispatchers.IO) {
            val photoItems: MutableList<PhotoItem> = ArrayList()
            photoItems.addAll(roomDatabase.myFlickrLocalDao().findPhotosByTitle("%$searchTerm%"))
            photoItems
        }

}
