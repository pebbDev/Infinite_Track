package com.example.infinite_track.data.soucre.network.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class AttendanceHistoryResponseItem(

	@field:SerializedName("upload_image")
	val uploadImage: String? = null,

	@field:SerializedName("notes")
	val notes: String? = null,

	@field:SerializedName("check_in_time")
	val checkInTime: String? = null,

	@field:SerializedName("check_out_time")
	val checkOutTime: String? = null,

	@field:SerializedName("attendance_status_id")
	val attendanceStatusId: Int? = null,

	@field:SerializedName("attendance_category_id")
	val attendanceCategoryId: Int? = null,

	@field:SerializedName("attendance_date")
	val attendanceDate: String? = null,

	@field:SerializedName("attendance_month_year")
	val attendanceMonthYear: String? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("attendanceId")
	val attendanceId: Int? = null,
) : Parcelable
