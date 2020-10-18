package com.elysiant.myflickr.data.service

import com.elysiant.myflickr.models.PhotoItem
import com.elysiant.myflickr.models.PhotosResponse
import io.reactivex.Observable
import retrofit2.adapter.rxjava2.Result

/**
 * Created by vicsonvictor on 10/17/2020.
 */
interface FlickrApi {

    // Fetch Photos
    fun searchPhotos(searchTerm: String, pageNo: Int): Observable<Result<PhotosResponse>>

    // Save Photos - TODO: applies only to local storage, may be there is a better way to place it elsewhere.
    fun savePhotos(photoItems: List<PhotoItem>)

    companion object {
        const val READ_TIMEOUT = 5000 //ms
    }
}
