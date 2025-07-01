package com.example.infinite_track.data.soucre.network.response

import com.google.gson.annotations.SerializedName

// Kelas utama yang membungkus seluruh respons
data class TodayStatusResponse(
    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("data")
    val data: TodayStatusData,

    // Walaupun tidak ada di contoh, message biasanya ada.
    // Dibuat nullable untuk keamanan.
    @field:SerializedName("message")
    val message: String? = null
)

// Kelas yang merepresentasikan objek "data"
data class TodayStatusData(
    @field:SerializedName("can_check_in")
    val canCheckIn: Boolean,

    @field:SerializedName("can_check_out")
    val canCheckOut: Boolean?, // Nullable karena bisa null di JSON

    @field:SerializedName("checked_in_at")
    val checkedInAt: String?, // Nullable karena bisa null di JSON

    @field:SerializedName("checked_out_at")
    val checkedOutAt: String?, // Nullable karena bisa null di JSON

    @field:SerializedName("active_mode")
    val activeMode: String,

    @field:SerializedName("active_location")
    val activeLocation: ActiveLocation?, // Dibuat nullable untuk keamanan

    @field:SerializedName("today_date")
    val todayDate: String,

    @field:SerializedName("is_holiday")
    val isHoliday: Boolean,

    @field:SerializedName("holiday_checkin_enabled")
    val holidayCheckinEnabled: Boolean,

    @field:SerializedName("current_time")
    val currentTime: String,

    @field:SerializedName("checkin_window")
    val checkinWindow: CheckinWindow,

    @field:SerializedName("checkout_auto_time")
    val checkoutAutoTime: String
)


data class ActiveLocation(
    @field:SerializedName("location_id")
    val locationId: Int,

    @field:SerializedName("latitude")
    val latitude: Double,

    @field:SerializedName("longitude")
    val longitude: Double,

    @field:SerializedName("radius")
    val radius: Int,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("category")
    val category: String
)

// Kelas untuk objek "checkin_window" yang bersarang
data class CheckinWindow(
    @field:SerializedName("start_time")
    val startTime: String,

    @field:SerializedName("end_time")
    val endTime: String
)
