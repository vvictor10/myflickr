package com.elysiant.myflickr.data.service.network

import android.annotation.SuppressLint
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.Result
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException
import java.lang.reflect.Type

/**
 * A custom CallAdapterFactory provides a way to intercept the response and handle errors as needed.
 */
class MyFlickrRxJavaCallAdapterFactory private constructor() : CallAdapter.Factory() {
    private val original: RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *> {
        @Suppress("UNCHECKED_CAST")
        return RxCallAdapterWrapper(retrofit, original.get(returnType, annotations, retrofit) as CallAdapter<Any, Observable<Result<Any>>>) // TODO: Find better option for unchecked cast
    }

    private class RxCallAdapterWrapper<R>(private val retrofit: Retrofit, private val wrapped: CallAdapter<R, Observable<Result<Any>>>) : CallAdapter<R, Observable<Result<Any>>> {

        override fun responseType(): Type {
            return wrapped.responseType()
        }

        @SuppressLint("CheckResult")
        override fun adapt(call: Call<R>): Observable<Result<Any>> {
            // Lambda version
            return (wrapped.adapt(call) as Observable<Result<Any>>)
                    .flatMap {
                        val response = it.response()
                        if (response != null && response.isSuccessful) {
                            Observable.just(it)
                        } else {
                            if (it.isError) {
                                if (it.error() is IOException) {
                                    Observable.error(MyFlickrRetrofitException.networkError(it.error() as IOException))
                                } else {
                                    Observable.error(MyFlickrRetrofitException.httpError(call.request().url().toString(), null,  retrofit))
                                }
                            } else {
                                Observable.error(MyFlickrRetrofitException.unexpectedError(Throwable("Something went wrong. Please try again later.")))
                            }
                        }
                    }
        }

    }

    companion object {
        fun create(): CallAdapter.Factory {
            return MyFlickrRxJavaCallAdapterFactory()
        }
    }
}