package com.elysiant.myflickr.domain.interactors

import com.elysiant.myflickr.data.service.FlickrApi
import com.elysiant.myflickr.domain.interactors.PhotosDataInteractor
import com.elysiant.myflickr.models.PhotosResponse
import io.reactivex.Observable
import retrofit2.adapter.rxjava2.Result
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A component to host all business logic associated with Venue data.
 * Acts as a facade between the data service layer and the UI code components.
 */
@Singleton
class PhotosDataInteractorImpl @Inject
constructor(private val flickrApi: FlickrApi) :
    PhotosDataInteractor {

    /**
     * Used to search for photos based on a search string.
     */
    override fun searchForPhotos(queryTerm: String, pageNo: Int): Observable<Result<PhotosResponse>> {
        return flickrApi.searchPhotos(queryTerm, pageNo)
    }

}
