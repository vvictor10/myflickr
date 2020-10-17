package com.elysiant.myflickr.domain.interactors

import com.elysiant.myflickr.models.PhotosResponse
import io.reactivex.Observable
import retrofit2.adapter.rxjava2.Result

interface PhotosDataInteractor {

    /**
     * Used to search for photos based on a search string.
     */
    fun searchForPhotos(queryTerm: String, pageNo: Int): Observable<Result<PhotosResponse>>

}
