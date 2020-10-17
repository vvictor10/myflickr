package com.elysiant.myflickr.data.service

import com.elysiant.myflickr.models.PhotosResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.Query

interface PhotosService {

    @GET("rest/")
    fun searchPhotos(@Query("method") method: String, @Query("text") queryTerm: String
                     , @Query("extras") extras: String
                     , @Query("per_page") perPageCount: Int
                     , @Query("page") pageNo: Int): Observable<Result<PhotosResponse>>

//    @GET("venues/search?intent=checkin&limit=25")
//    fun search(@Query("query") queryTerm: String): Observable<Result<VenuesResponse>>
//
//    @GET("venues/suggestcompletion?intent=checkin&limit=50")
//    fun suggestCompletion(@Query("query") queryTerm: String): Observable<Result<SuggestedVenuesResponse>>
//
//    @GET("venues/{venue_id}")
//    fun venue(@Path("venue_id") venueId: String): Observable<Result<VenueResponse>>

}
