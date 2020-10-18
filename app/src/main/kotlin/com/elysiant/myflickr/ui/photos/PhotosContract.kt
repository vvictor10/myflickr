package com.elysiant.myflickr.ui.photos

import com.elysiant.myflickr.models.PhotoItem
import com.elysiant.myflickr.models.Photos

/**
 * This interface defines the data dependency contract between the Photos
 * Views(e.g. [SearchActivity]) and the Presenters (e.g. [PhotosPresenter]).
 *
 * Created by vicsonvictor on 10/17/2020.
 */
interface PhotosContract {

    interface View {
        fun onSearch(photos: Photos, pageNo: Int)
        fun onPhoto(photoItem: PhotoItem)
        fun onError()
    }

    interface Presenter {
        fun bindView(view: View)
        fun unBindView()
        fun doSearch(searchTerm: String)
        fun doSearchNextPage(searchTerm: String, pageNo: Int): Boolean
        fun clearNextSearchPageInfo()
    }
}

