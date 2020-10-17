package com.elysiant.myflickr.data.service

import com.elysiant.myflickr.models.PhotosResponse
import io.reactivex.Observable
import retrofit2.adapter.rxjava2.Result
/**
 * Created by vicsonvictor on 10/17/2020.
 */

interface FlickrApi {

    // Fetch Photos
    fun searchPhotos(searchTerm: String, pageNo: Int): Observable<Result<PhotosResponse>>

    companion object {
        const val READ_TIMEOUT = 5000 //ms
    }
}
