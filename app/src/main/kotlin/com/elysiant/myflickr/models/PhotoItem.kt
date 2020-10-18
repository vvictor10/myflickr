package com.elysiant.myflickr.models

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "photo_item")
class PhotoItem(

	@ColumnInfo(name = "owner")
	@field:SerializedName("owner")
	val owner: String? = null,

	@ColumnInfo(name = "server")
	@field:SerializedName("server")
	val server: String? = null,

	@ColumnInfo(name = "height_s")
	@field:SerializedName("height_s")
	val heightS: Int? = null,

	@ColumnInfo(name = "width_s")
	@field:SerializedName("width_s")
	val widthS: Int? = null,

	@ColumnInfo(name = "url_s")
	@field:SerializedName("url_s")
	val smallUrl: String? = null,

	@ColumnInfo(name = "url_c")
	@field:SerializedName("url_c")
	val largeUrl: String? = null,

	@ColumnInfo(name = "is_public")
	@field:SerializedName("ispublic")
	val ispublic: Int? = null,

	@ColumnInfo(name = "is_friend")
	@field:SerializedName("isfriend")
	val isfriend: Int? = null,

	@ColumnInfo(name = "farm")
	@field:SerializedName("farm")
	val farm: Int? = null,

	@NonNull
	@ColumnInfo(name = "id")
	@PrimaryKey
	@field:SerializedName("id")
	val id: String,

	@ColumnInfo(name = "secret")
	@field:SerializedName("secret")
	val secret: String? = null,

	@ColumnInfo(name = "title")
	@field:SerializedName("title")
	val title: String? = null,

	@ColumnInfo(name = "is_family")
	@field:SerializedName("isfamily")
	val isfamily: Int? = null

)