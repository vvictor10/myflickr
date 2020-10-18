package com.elysiant.myflickr.data.service.local

import com.elysiant.myflickr.data.service.FlickrApi
import com.elysiant.myflickr.models.PhotoItem
import com.elysiant.myflickr.models.PhotosResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result
import java.io.*

class FlickrApiTest(private val resourcesDirectory: String) : FlickrApi {

    private val gsonInstance: Gson
        get() = GsonBuilder().create()

    init {
        println(resourcesDirectory)
    }

    override fun searchPhotos(searchTerm: String, pageNo: Int): Observable<Result<PhotosResponse>> {
        println("searchPhotos: $resourcesDirectory")
        val jsonString = fetchJsonFromFile("$resourcesDirectory/search_results.json")
        return Observable.just(Result.response(Response.success(gsonInstance.fromJson(jsonString, PhotosResponse::class.java))))
    }

    override fun savePhotos(photoItems: List<PhotoItem>) {
        TODO("Not applicable")
    }

    companion object {

        fun fetchJsonFromFile(filePath: String): String? {
            println(filePath)
            val writer = StringWriter()
            var reader: Reader? = null
            try {
                reader = BufferedReader(FileReader(filePath))

                val buffer = CharArray(1024)
                var n: Int = reader.read(buffer)
                while (n != -1) {
                    writer.write(buffer, 0, n)
                    n = reader.read(buffer)
                }
                reader.close()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                return null
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                if (reader != null) {
                    try {
                        reader.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        return null
                    }

                }
            }
            return writer.toString()
        }
    }
}
