package com.elysiant.myflickr.ui.photos

import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.LruCache
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import butterknife.OnClick
import com.elysiant.myflickr.R
import com.elysiant.myflickr.common.MyFlickrConstants
import com.elysiant.myflickr.models.PhotoItem
import com.elysiant.myflickr.models.Photos
import com.elysiant.myflickr.ui.common.BaseNavigationActivity
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.content_search.*
import kotlinx.android.synthetic.main.search_toolbar.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 *  Loading and referring to classes in the feature module.
 *
 *  https://github.com/googlesamples/android-dynamic-code-loading
 */
open class SearchActivity : BaseNavigationActivity(), PhotosContract.View,
    SearchResultsAdapter.PhotoItemListener {

    @Inject
    lateinit var picasso: Picasso
    @Inject
    lateinit var photosPresenter: PhotosPresenter
    @Inject
    lateinit var lruCache: LruCache<Any, Any>

    private lateinit var searchResultsAdapter: SearchResultsAdapter

    private lateinit var searchResultsLayoutManager: GridLayoutManager
    private lateinit var searchInput: String
    private lateinit var suggestedSearchTermsLayoutManager: LinearLayoutManager

    private var photos: Photos? = null
    private var photoItems: MutableList<PhotoItem> = ArrayList()

    private var handler = Handler()

    // pagination
    private var isLoading = false
    private var totalItemCount = 0
    private var previousTotal = 0
    private var lastVisibleItemPosition = 0
    private var abortPaginationDataUpdate = false
    private var loadingStateBeforePaginationCall = false
    private var lastVisibleItemPositionBeforePaginationCall = 0
    private var totalItemCountBeforePaginationCall = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        ButterKnife.bind(this)

        component().inject(this)

        setupNavigationView()

        setupDrawerListeners()

        initToolbar(findViewById<View>(R.id.toolbar) as Toolbar)

        initSearchTextView()

        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        setupSearchResultsRecyclerView()

        displayNoResultsState(false)
    }

    override fun onPause() {
        photosPresenter.unBindView()
        hideKeyboard()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        photosPresenter.bindView(this)
    }

    @OnClick(R.id.clear_icon)
    fun onClearMenuClicked() {
        search_edittext.setText("")
        search_edittext.setSelection(0)
        search_edittext.isCursorVisible = true
        search_edittext.isEnabled = true
        showKeyboard()

        displayNoResultsState(false)
    }

    @OnClick(R.id.search_edittext)
    fun onSearchEditTextClicked() {
        search_edittext.isCursorVisible = true
        search_edittext.isEnabled = true
    }

    override fun onSearch(photos: Photos, pageNo: Int) {
        Timber.i("Search Results for: %s, pageNo: %d, total pages: %d", search_edittext.text.trim(), pageNo, photos.totalNoOfPages)
        if (pageNo > 1) { // Pagination response
            showNextPageResults(photos)
        } else { // fresh search
            if (photos.photosList.isNotEmpty()) {
                displayNoResultsState(false)
                this.photos = photos
                this.photoItems.clear()
                this.photoItems.addAll(photos.photosList)
                Timber.d("Updating data - size: %d", photoItems.size)
                searchResultsLayoutManager.scrollToPosition(0)
                searchResultsAdapter.updateData(photoItems)
                handler.postDelayed(Runnable { displayLoadingIndicator(false) }, 1000)
            } else {
                Timber.i("Empty data or bad response")
                searchResultsLayoutManager.scrollToPosition(0)
                searchResultsAdapter.updateData(ArrayList())
                displayLoadingIndicator(false)
                displayNoResultsState(true)
            }
        }
    }

    override fun onPhoto(photoItem: PhotoItem) {
        TODO("Not yet implemented -  Must go to full screen view")
    }

    override fun onError() {
        displayLoadingIndicator(false)
        cancelPaginationLoadingState(true)
        displaySnackBarMessage("Something went wrong. Please try again later!")
    }

    override fun getNavigationView(): NavigationView {
        return nav_view
    }

    override fun getDrawerLayout(): DrawerLayout {
        return drawer_layout
    }

    override fun onPhotoItemClicked(photoItem: PhotoItem) {
        goToFullScreenPhotoActivity(photoItem)
    }

    private fun doSearch(searchString: String) {
        Timber.d("called with %s", searchString)
        if (searchString.isNotEmpty()) {
            Timber.d("Searching for %s", searchString)
            this.searchInput = searchString

            // close the keyboard and disable cursor
            hideKeyboard()
            search_edittext.isCursorVisible = false

            resetPaginationAttributes()

            displayLoadingIndicator(true)
            photosPresenter.doSearch(searchString)
        }
    }

    private fun resetPaginationAttributes() {
        totalItemCount = 0
        previousTotal = 0
        totalItemCountBeforePaginationCall = 0
        lastVisibleItemPosition = 0
        abortPaginationDataUpdate = false
        loadingStateBeforePaginationCall = false
        lastVisibleItemPositionBeforePaginationCall = 0
    }

    private fun showNextPageResults(photosResult: Photos) {
        if (photosResult.photosList.isNotEmpty()) {
            photos = photosResult
            photoItems.addAll(photosResult.photosList)
            photos?.let {
                showNextResults(it.photosList.toMutableList())
            }
        }
    }

    private fun showNextResults(photoItems: MutableList<PhotoItem>) {
        if (abortPaginationDataUpdate) {
            return
        }
        searchResultsAdapter.hideLoadingMoreState()
        searchResultsAdapter.appendData(photoItems)
    }

    /**
     * Initializes the adapter/recycler view to display search results
     */
    private fun setupSearchResultsRecyclerView() {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        searchResultsAdapter = SearchResultsAdapter(this, this, picasso, size.x)
        searchResultsAdapter.setHasStableIds(true)
        searchResultsLayoutManager = GridLayoutManager(this, 3)

        search_results_recycler_view.layoutManager = searchResultsLayoutManager
        search_results_recycler_view.adapter = searchResultsAdapter

        search_results_recycler_view.setItemViewCacheSize(60)

        search_results_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) { // skip call on scroll down action
                    fetchNextPageOfSearchResults()
                }
            }
        })
    }

    /**
     * Identifies if end of current list has been reached and makes the server call
     * to fetch the next page if it exists.
     */
    private fun fetchNextPageOfSearchResults() {
        totalItemCount = searchResultsLayoutManager.itemCount
         lastVisibleItemPosition = searchResultsLayoutManager.findLastVisibleItemPosition()
        if (isLoading) {
            if (totalItemCount > previousTotal) {
                isLoading = false
                previousTotal = totalItemCount
            }
        }
        if (!isLoading && lastVisibleItemPosition == totalItemCount - 1) {
            // End has been reached but check if there is next
            val searchString = search_edittext.text.toString().trim { it <= ' ' }
            photos?.let {
                if (it.pageNo < it.totalNoOfPages) {
                    isLoading = true
                    val validRequest: Boolean = photosPresenter.doSearchNextPage(searchString, it.pageNo + 1)
                    if (validRequest) {
                        Timber.d("Fetching next page for String(page: %d): %s", it.pageNo + 1, searchString)
                        abortPaginationDataUpdate = false
                        loadingStateBeforePaginationCall = false
                        totalItemCountBeforePaginationCall = totalItemCount
                        lastVisibleItemPositionBeforePaginationCall = lastVisibleItemPosition

                        searchResultsAdapter.showLoadingMoreState()
                    }
                }
            }
        }
    }

    private fun cancelPaginationLoadingState(forceCancellation: Boolean) {
        if (isLoading || forceCancellation) {
            abortPaginationDataUpdate = true
            isLoading = loadingStateBeforePaginationCall
            totalItemCount = totalItemCountBeforePaginationCall
            lastVisibleItemPosition = lastVisibleItemPositionBeforePaginationCall

            searchResultsAdapter.hideLoadingMoreState()

            if (!forceCancellation) {
                photosPresenter.clearNextSearchPageInfo()
            }
        }
    }

    private fun initSearchTextView() {
        search_edittext.clearComposingText()
        search_edittext.setText("")
        search_edittext.setSelection(0)

        search_edittext.addTextChangedListener(searchTextWatcher)
        search_edittext.setOnEditorActionListener(getOnEditorActionListener())
    }

    private fun displayLoadingIndicator(show: Boolean) {
        when (show) {
            true -> loading_indicator.visibility = View.VISIBLE
            false -> loading_indicator.visibility = View.GONE
        }
    }

    private fun getOnEditorActionListener(): TextView.OnEditorActionListener {
        return TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchString = search_edittext.text.toString().trim { it <= ' ' }
                Timber.d("OnEditorActionListener User Action|%s|%s|%s", "Search for Photos", "Search String", searchString)
                doSearch(searchString)
                return@OnEditorActionListener true
            }
            false
        }
    }

    private val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // make sure menus are initialized since text watcher is created before menus
//            clear_icon.visibility = when (s.isNotEmpty()) {
//                true -> View.VISIBLE
//                false -> View.INVISIBLE
//            }

            searchInput = s.toString()
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private fun displayNoResultsState(show: Boolean) {
        when (show) {
            true -> {
                no_results_message.visibility = View.VISIBLE
                no_results_message.text = String.format(getString(R.string.search_no_results_response),
                    searchInput)
            }
            false -> no_results_message.visibility = View.INVISIBLE
        }
    }

}