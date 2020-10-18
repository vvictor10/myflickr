package com.elysiant.myflickr.domain.interactors.mocks

import com.elysiant.myflickr.data.service.test.FlickrApiTest
import com.elysiant.myflickr.domain.interactors.PhotosDataInteractor
import com.elysiant.myflickr.models.PhotosResponse
import io.reactivex.Observable
import retrofit2.adapter.rxjava2.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class MockPhotosDataInteractor @Inject
constructor(private val flickrApi: FlickrApiTest) : PhotosDataInteractor {

    /**
     * Used to search for photos based on a search string.
     */
    override fun searchForPhotos(queryTerm: String, pageNo: Int): Observable<Result<PhotosResponse>> {
        return flickrApi.searchPhotos(queryTerm, pageNo)
    }

}
