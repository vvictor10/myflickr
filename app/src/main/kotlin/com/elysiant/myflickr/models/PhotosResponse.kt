package com.elysiant.myflickr.models

import com.google.gson.annotations.SerializedName

class PhotosResponse(

	@field:SerializedName("stat")
	val stat: String? = null,

	@field:SerializedName("photos")
	val photos: Photos? = null
)