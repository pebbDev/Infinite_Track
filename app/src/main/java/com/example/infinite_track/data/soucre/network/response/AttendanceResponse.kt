package com.example.infinite_track.data.soucre.network.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class AttendanceResponse(
	@field:SerializedName("attendance_status")
	val attendanceStatus: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("attendanceId")
	val attendanceId: Int? = null,
) : Parcelable
