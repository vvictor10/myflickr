package com.elysiant.myflickr.data.service.network

import com.elysiant.myflickr.common.MyFlickrConstants
import com.elysiant.myflickr.data.service.FlickrApi
import com.elysiant.myflickr.data.service.PhotosService
import com.elysiant.myflickr.models.PhotoItem
import com.elysiant.myflickr.models.PhotosResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.Result
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by vicsonvictor on 10/17/2020.
 */
class FlickrApiRetrofit(private val baseUrl: String, private val apiKey: String) : FlickrApi {

    private val retrofit: Retrofit

    private lateinit var callAdapterFactory: CallAdapter.Factory

    private val gsonInstance: Gson
        get() = GsonBuilder().create()

    init {
        retrofit = newInstance(baseUrl)
    }

    private fun newInstance(baseUrl: String): Retrofit {
        val gson = gsonInstance

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor { chain ->
                    var request = chain.request()

                    val urlBuilder = request.url().newBuilder()

                    // add client id & other parameters
                    urlBuilder.addQueryParameter(MyFlickrConstants.API_KEY, apiKey).build()
                    urlBuilder.addQueryParameter(MyFlickrConstants.RESPONSE_FORMAT,
                        MyFlickrConstants.RESPONSE_FORMAT_VALUE).build()
                    urlBuilder.addQueryParameter(MyFlickrConstants.NO_JSON_CALLBACK, "1").build()

                    request = request.newBuilder().url(urlBuilder.build()).build()

                    chain.proceed(request)
                }
                .readTimeout(FlickrApi.READ_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .build()

        callAdapterFactory = MyFlickrRxJavaCallAdapterFactory.create()

        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl) //
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(callAdapterFactory)
                .build()

    }

    override fun searchPhotos(searchTerm: String, pageNo: Int): Observable<Result<PhotosResponse>> {
        return retrofit.create(PhotosService::class.java).searchPhotos(MyFlickrConstants.PHOTOS_METHOD,
            searchTerm, MyFlickrConstants.PHOTOS_EXTRAS, MyFlickrConstants.PHOTOS_PER_PAGE, pageNo)
    }

    override fun savePhotos(photoItems: List<PhotoItem>) {
        TODO("Not applicable")
    }

}
