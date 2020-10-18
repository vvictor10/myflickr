package com.elysiant.myflickr.app.injection.component

import android.util.LruCache
import com.elysiant.myflickr.app.MyFlickrApplication
import com.elysiant.myflickr.app.injection.module.MyFlickrModule
import com.elysiant.myflickr.data.localstorage.MyFlickrRoomDatabase
import com.elysiant.myflickr.data.service.room.FlickrApiLocalStorage
import com.elysiant.myflickr.domain.interactors.MyFlickrStartupDataInteractor
import com.elysiant.myflickr.domain.interactors.PhotosDataInteractor
import com.elysiant.myflickr.service.MyFlickrStartupIntentService
import com.squareup.picasso.Picasso
import dagger.Component
import javax.inject.Singleton

/**
 * Created by vicsonvictor on 10/17/2020.
 */
@Singleton
@Component(modules = [MyFlickrModule::class])
interface MyFlickrComponent {

    fun provideRoomDatabase(): MyFlickrRoomDatabase

    fun provideFlickrApiLocalStorage(): FlickrApiLocalStorage

    fun providePhotosDataInteractor(): PhotosDataInteractor

    fun provideMyFlickrStartupDataInteractor(): MyFlickrStartupDataInteractor

    fun providePicasso(): Picasso

    fun provideLruCache(): LruCache<Any, Any>

    // injects
    fun inject(app: MyFlickrApplication)

    fun inject(myFlickrStartupIntentService: MyFlickrStartupIntentService)

}
