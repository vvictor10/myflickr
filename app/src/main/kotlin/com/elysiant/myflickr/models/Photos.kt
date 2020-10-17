package com.elysiant.myflickr.models

import com.google.gson.annotations.SerializedName

class Photos(

	@field:SerializedName("perpage")
	val perPageCount: Int? = null,

	@field:SerializedName("total")
	val total: String? = null,

	@field:SerializedName("pages")
	val totalNoOfPages: Int = 0,

	@field:SerializedName("photo")
	val photosList: List<PhotoItem> = ArrayList(),

	@field:SerializedName("page")
	val pageNo: Int = 0
)