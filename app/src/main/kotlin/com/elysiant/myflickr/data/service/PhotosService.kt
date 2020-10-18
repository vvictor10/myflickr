package com.elysiant.myflickr.data.service

import com.elysiant.myflickr.models.PhotosResponse
import io.reactivex.Observable
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * All the REST calls for the Flickr service go here.
 */
interface PhotosService {

    @GET("rest/")
    fun searchPhotos(@Query("method") method: String, @Query("text") queryTerm: String
                     , @Query("extras") extras: String
                     , @Query("per_page") perPageCount: Int
                     , @Query("page") pageNo: Int): Observable<Result<PhotosResponse>>

}
