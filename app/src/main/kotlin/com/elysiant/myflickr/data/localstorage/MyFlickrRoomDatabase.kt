package com.elysiant.myflickr.data.localstorage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.elysiant.myflickr.data.localstorage.dao.MyFlickrPhotosRoomDao
import com.elysiant.myflickr.models.PhotoItem

@Database(entities = [PhotoItem::class], version = 1)
abstract class MyFlickrRoomDatabase : RoomDatabase() {
    abstract fun myFlickrLocalDao(): MyFlickrPhotosRoomDao
}