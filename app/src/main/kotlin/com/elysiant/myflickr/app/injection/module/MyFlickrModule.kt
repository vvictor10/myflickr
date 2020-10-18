package com.elysiant.myflickr.app.injection.module

import android.content.Context
import android.util.LruCache
import androidx.room.Room
import com.elysiant.myflickr.BuildConfig
import com.elysiant.myflickr.app.injection.qualifier.ForApplication
import com.elysiant.myflickr.common.MyFlickrConstants
import com.elysiant.myflickr.common.MyFlickrEnvironmentEnum
import com.elysiant.myflickr.data.localstorage.MyFlickrRoomDatabase
import com.elysiant.myflickr.data.service.FlickrApi
import com.elysiant.myflickr.data.service.network.FlickrApiRetrofit
import com.elysiant.myflickr.data.service.room.FlickrApiLocalStorage
import com.elysiant.myflickr.domain.interactors.MyFlickrStartupDataInteractor
import com.elysiant.myflickr.domain.interactors.MyFlickrStartupDataInteractorImpl
import com.elysiant.myflickr.domain.interactors.PhotosDataInteractor
import com.elysiant.myflickr.domain.interactors.PhotosDataInteractorImpl
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Singleton

/**
 * Created by vicsonvictor on 10/17/2020.
 */
@Module
open class MyFlickrModule(private val context: Context) {

    companion object {
        const val CACHE_SIZE = 5 * 1024 * 1024
    }

    private val mFlickr: FlickrApi by lazy {
        Timber.i("Creating new FlickrApiRetrofit instance.")
        FlickrApiRetrofit(
            MyFlickrEnvironmentEnum.PROD.flickrApiBaseUrl, BuildConfig.MY_FLICKR_API_KEY)
    }

    @ForApplication
    @Provides
    @Singleton
    fun provideContext(): Context {
        return context
    }

    @Provides
    @Singleton
    open fun provideLruCache(): LruCache<Any, Any> {
        return LruCache(CACHE_SIZE)
    }

    @Provides
    @Singleton
    open fun provideRoomDatabase(): MyFlickrRoomDatabase {
        return Room.databaseBuilder(context,
            MyFlickrRoomDatabase::class.java, context.packageName
        ).build()
    }

    @Provides
    @Singleton
    open fun provideFlickrApiLocalStorage(roomDatabase: MyFlickrRoomDatabase): FlickrApiLocalStorage {
        return FlickrApiLocalStorage(roomDatabase)
    }

    @Provides
    @Singleton
    fun provideFlickr(): FlickrApi {
        return mFlickr
    }

    @Provides
    @Singleton
    fun providePicasso(client: OkHttpClient): Picasso {
        return Picasso.Builder(context)
                .downloader(OkHttp3Downloader(client))
                .listener { _, uri, exception -> Timber.w(exception, "Failed to load image: %s", uri) }
                .build()
    }

    @Provides
    @Singleton
    open fun providePhotosDataInteractor(flickrApi: FlickrApi, flickrApiLocalStorage: FlickrApiLocalStorage): PhotosDataInteractor {
        return PhotosDataInteractorImpl(flickrApi, flickrApiLocalStorage)
    }

    @Provides
    @Singleton
    fun provideMyFlickrStartupDataInteractor(flickrApi: FlickrApi, lruCache: LruCache<Any, Any>): MyFlickrStartupDataInteractor {
        return MyFlickrStartupDataInteractorImpl(
            lruCache,
            flickrApi
        )
    }

    @Provides
    @Singleton
    internal fun provideOkHttpClient(): OkHttpClient {
        return createOkHttpClient(context).build()
    }

    /**
     * Creates a cache enabled [OkHttpClient] instance and returns it.
     * Currently, this is being only used for Picasso image handling and caching.
     */
    private fun createOkHttpClient(context: Context): OkHttpClient.Builder {
        // Install an HTTP cache in the application cache directory.
        val cacheDir = File(context.cacheDir, MyFlickrConstants.HTTP)
        val cache = Cache(cacheDir, MyFlickrConstants.IMAGE_DISK_CACHE_SIZE.toLong())

        return OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(MyFlickrConstants.HTTP_TIMEOUT_VALUE.toLong(), SECONDS)
                .readTimeout(MyFlickrConstants.HTTP_TIMEOUT_VALUE.toLong(), SECONDS)
                .writeTimeout(MyFlickrConstants.HTTP_TIMEOUT_VALUE.toLong(), SECONDS)
    }

}
