package com.elysiant.myflickr.ui.photos

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.elysiant.myflickr.R
import com.elysiant.myflickr.common.MyFlickrConstants
import com.elysiant.myflickr.models.PhotoItem
import com.elysiant.myflickr.ui.common.LoadingIndicatorView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.photo_item_view.view.*
import timber.log.Timber
import java.util.*

class SearchResultsAdapter(private val context: Context, private val listener: PhotoItemListener,
                           private val picasso: Picasso, screenWidth: Int) : RecyclerView.Adapter<ViewHolder>() {

    private var recyclerView: RecyclerView? = null
    private val imageSize: Int = (screenWidth * PHOTO_IMAGE_PERCENT).toInt()
    private var photoItems: MutableList<PhotoItem> = ArrayList()
    private var loadingIndicatorRow = -1
    private var loadingIndicatorView : LoadingIndicatorView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        lateinit var viewHolder: ViewHolder
        val inflater = LayoutInflater.from(parent.context)
        viewHolder = if (viewType == VIEW_TYPE_ITEM) {
            PhotoItemViewHolder(inflater.inflate(R.layout.photo_item_view, parent, false))
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.search_next_page_loading_indicator, parent, false)
            LoadingIndicatorViewHolder(view)
        }
        return viewHolder

    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (viewHolder is PhotoItemViewHolder) {
            setFadeAnimation(viewHolder.itemView)
            viewHolder.bind(context, photoItems[position], listener, picasso, imageSize)
        } else if (viewHolder is LoadingIndicatorViewHolder) {
            loadingIndicatorView = viewHolder.loadingIndicatorView
            loadingIndicatorView?.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return photoItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (photoItems[position].id == MyFlickrConstants.LOADING_MORE_PHOTO_ID_PLACE_HOLDER) {
            VIEW_TYPE_LOADING_PROGRESS
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun getItemId(position: Int): Long {
        val photo: PhotoItem? = photoItems[position]
        return photo?.id?.toLong() ?: 0
    }

    override fun onViewDetachedFromWindow(@NonNull holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    fun updateData(photos: List<PhotoItem>) {
        photos.let {
            this.photoItems = ArrayList(photos)
            notifyDataSetChanged()
        }
    }

    fun appendData(photos: List<PhotoItem>) {
        this.photoItems.addAll(photos)
        Timber.d("Total no. of products now %d", this.photoItems.size)
    }

    interface PhotoItemListener {
        fun onPhotoItemClicked(photoItem: PhotoItem)
    }

    class PhotoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val photoItemImageView: ImageView = itemView.photo_image
        var picassoTarget: Target? = null

        private fun loadImage(context: Context, imageView: ImageView, photoItem: PhotoItem, picasso: Picasso) {

            if (photoItem.smallUrl.isNullOrBlank()) {
                imageView.setImageDrawable(context.getDrawable(android.R.drawable.stat_notify_error))
                return
            }

            val imageUrl = photoItem.smallUrl
            if (picassoTarget == null) {
                picassoTarget = object: Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                    override fun onBitmapFailed(errorDrawable: Drawable?) {
                        Timber.w("Failed to load image with url %s", imageUrl)
                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        photoItemImageView.setImageBitmap(bitmap)
                    }
                }
            }

            picasso.load(imageUrl).into(picassoTarget)
        }

        fun bind(context: Context, data: PhotoItem?, listener: PhotoItemListener, picasso: Picasso, imageSize: Int) {
            data?.let {
                val layoutParams = photoItemImageView.layoutParams
                layoutParams.height = imageSize
                layoutParams.width = imageSize
                photoItemImageView.layoutParams = layoutParams

                // reset the target
                this.picassoTarget = null

                // load category image
                loadImage(context, photoItemImageView, data, picasso)

                photoItemImageView.setOnClickListener {
                    listener.onPhotoItemClicked(data)
                }
            }
        }
    }

    class LoadingIndicatorViewHolder constructor(v: View) : RecyclerView.ViewHolder(v) {
        var loadingIndicatorView: LoadingIndicatorView = v.findViewById(R.id.loading_indicator)
    }

    /**
     * Shows Loading indicator.
     */
    fun showLoadingMoreState() {
        photoItems.add(PhotoItem(id = MyFlickrConstants.LOADING_MORE_PHOTO_ID_PLACE_HOLDER))
        loadingIndicatorRow = photoItems.size - 1
        recyclerView?.post {
            notifyItemInserted(photoItems.size - 1)
        }
    }

    /**
     * Hides Loading indicator.
     */
    fun hideLoadingMoreState() {
        if (loadingIndicatorView != null) {
            loadingIndicatorView?.visibility = View.GONE
            photoItems.removeAt(photoItems.size - 1)
            recyclerView?.post {
                notifyItemRemoved(photoItems.size)
            }
        }
        loadingIndicatorRow = -1
    }

    private fun setFadeAnimation(view: View) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 500
        view.startAnimation(anim)
    }

    companion object {
        const val PHOTO_IMAGE_PERCENT = 0.333

        // view types
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING_PROGRESS = 1
    }

}
