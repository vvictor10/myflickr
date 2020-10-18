package com.elysiant.myflickr.models

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity
class PhotosResponse(

	@field:SerializedName("stat")
	val stat: String? = null,

	@field:SerializedName("photos")
	val photos: Photos? = null
)