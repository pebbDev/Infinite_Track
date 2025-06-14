package com.example.infinite_track.data.soucre.network.request

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class AttendanceRequest(
	@field:SerializedName("attendance_category")
	val attendanceCategory: String? = null,

	@field:SerializedName("notes")
	val notes: String? = null,

	@field:SerializedName("latitude")
	val latitude: Float?,


	@field:SerializedName("longitude")
	val longitude: Float?,

	@field:SerializedName("action")
	val action: String? = null,

) : Parcelable
