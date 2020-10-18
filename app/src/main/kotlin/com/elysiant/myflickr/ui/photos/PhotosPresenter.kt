package com.elysiant.myflickr.ui.photos

import com.elysiant.myflickr.domain.interactors.PhotosDataInteractor
import com.elysiant.myflickr.models.PhotosResponse
import com.elysiant.myflickr.ui.scope.ActivityScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.adapter.rxjava2.Result
import timber.log.Timber
import javax.inject.Inject

/**
 * This class acts as the proxy to the interaction layer. The Activities and
 * other views need to interact with data, go through these proxies.
 */
@ActivityScope
class PhotosPresenter @Inject
constructor(private val photosDataInteractor: PhotosDataInteractor) : PhotosContract.Presenter {

    private val lastFailedSearchNextHref: String? = null
    private var searchTerm: String? = null
    private var currentSearchNextPageNo: Int? = null
    private var lastFailedSearchPageNo: Int? = null

    private val disposables = CompositeDisposable()
    private var viewListener: PhotosContract.View? = null

    override fun bindView(view: PhotosContract.View) {
        this.viewListener = view
    }

    override fun unBindView() {
        unsubscribe()
        this.viewListener = null
    }

    override fun doSearch(searchTerm: String) {
        this.searchTerm = searchTerm
        resetState()
        disposables.add(getSearchSubscription(searchTerm, 1))
    }

    override fun doSearchNextPage(searchTerm: String, pageNo: Int): Boolean {
        if(isRetryingFailedNextSearch(pageNo) || isNewNextSearch(pageNo)) {
            currentSearchNextPageNo = pageNo
            lastFailedSearchPageNo = null
            disposables.add(getSearchSubscription(searchTerm, pageNo))
            return true
        }
        return false
    }

    override fun clearNextSearchPageInfo() {
        currentSearchNextPageNo = null
    }

    private fun resetState() {
        currentSearchNextPageNo = null
        lastFailedSearchPageNo = null
    }

    /**
     * Indicates if a failed 'next' page search is being requested.
     */
    private fun isRetryingFailedNextSearch(pageNo: Int): Boolean {
        return currentSearchNextPageNo != null
                && lastFailedSearchPageNo != null
                && currentSearchNextPageNo == lastFailedSearchPageNo &&
                currentSearchNextPageNo == pageNo
    }

    /**
     * Indicates if a new 'next' page search is being requested.
     */
    private fun isNewNextSearch(pageNo: Int): Boolean {
        return currentSearchNextPageNo == null || currentSearchNextPageNo != pageNo
    }

    private fun unsubscribe() {
        disposables.clear()
    }

    private fun getSearchSubscription(searchTerm: String, pageNo: Int): Disposable {
        return photosDataInteractor.searchForPhotos(searchTerm, pageNo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(SearchSubscriber(viewListener, pageNo))
    }

    class SearchSubscriber(private val listener: PhotosContract.View?, private val pageNo: Int) : DisposableObserver<Result<PhotosResponse>>() {

        override fun onComplete() {
            // n/a
        }

        override fun onError(e: Throwable) {
            listener?.onError()
        }

        override fun onNext(result: Result<PhotosResponse>) {
            val photosResponse = result.response()?.body()
            photosResponse?.photos?.let {
                Timber.i("No. of photos received in search results: %d", photosResponse.photos.photosList.size)
                listener?.onSearch(it, pageNo)
            }
        }
    }

}
