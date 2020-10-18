package com.elysiant.myflickr.data.localstorage.dao

import androidx.room.*
import com.elysiant.myflickr.models.PhotoItem

@Dao
interface MyFlickrPhotosRoomDao {

    /**
     * TODO: Currently, this method erases the old photos and ONLY saves the latest ones sent down.
     * May be we can store to a particular limit and not limit to the last fetched set.
     */
    @Transaction
    suspend fun savePhotos(photoItems: List<PhotoItem>) {
        deleteAll()
        insertAll(photoItems)
    }

    @Query("SELECT * FROM photo_item")
    suspend fun getAllPhotos(): List<PhotoItem>

    /* use title = "%search_phrase%" to do a 'contains' search */
    @Query("SELECT * FROM photo_item WHERE title LIKE :title")
    suspend fun findPhotosByTitle(title: String): List<PhotoItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(photoItems: List<PhotoItem>)

    @Query("DELETE FROM photo_item")
    suspend fun deleteAll()
}