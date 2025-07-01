package com.example.infinite_track.data.mapper.attendance

import com.example.infinite_track.data.soucre.network.request.AttendanceRequest
import com.example.infinite_track.data.soucre.network.response.ActiveLocation
import com.example.infinite_track.data.soucre.network.response.AttendanceData
import com.example.infinite_track.data.soucre.network.response.AttendanceItem
import com.example.infinite_track.data.soucre.network.response.TodayStatusData
import com.example.infinite_track.domain.model.attendance.ActiveAttendanceSession
import com.example.infinite_track.domain.model.attendance.AttendanceRecord
import com.example.infinite_track.domain.model.attendance.AttendanceRequestModel
import com.example.infinite_track.domain.model.attendance.CheckinWindow
import com.example.infinite_track.domain.model.attendance.Location
import com.example.infinite_track.domain.model.attendance.TodayStatus
import com.example.infinite_track.data.soucre.network.response.CheckinWindow as CheckinWindowDto

/**
 * Extension function to convert TodayStatusData (DTO) to TodayStatus (Domain model)
 */
fun TodayStatusData.toDomain(): TodayStatus {
    return TodayStatus(
        canCheckIn = this.canCheckIn,
        canCheckOut = this.canCheckOut,
        checkedInAt = this.checkedInAt,
        checkedOutAt = this.checkedOutAt,
        activeMode = this.activeMode,
        activeLocation = this.activeLocation?.toDomain(),
        todayDate = this.todayDate,
        isHoliday = this.isHoliday,
        holidayCheckinEnabled = this.holidayCheckinEnabled,
        currentTime = this.currentTime,
        checkinWindow = this.checkinWindow.toDomain(),
        checkoutAutoTime = this.checkoutAutoTime
    )
}

/**
 * Extension function to convert CheckinWindow (DTO) to CheckinWindow (Domain model)
 */
fun CheckinWindowDto.toDomain(): CheckinWindow {
    return CheckinWindow(
        startTime = this.startTime,
        endTime = this.endTime
    )
}

/**
 * Extension function to convert ActiveLocation (DTO) to Location (Domain model)
 */
fun ActiveLocation.toDomain(): Location {
    return Location(
        locationId = this.locationId,
        description = this.description ?: "",
        latitude = this.latitude,
        longitude = this.longitude,
        radius = this.radius,
        category = this.category
    )
}

/**
 * Extension function to convert AttendanceData (DTO) to ActiveAttendanceSession (Domain model)
 * Used for newly created or updated attendance sessions
 */
fun AttendanceData.toActiveSession(): ActiveAttendanceSession {
    return ActiveAttendanceSession(
        idAttendance = this.idAttendance,
        userId = this.userId,
        categoryId = this.categoryId,
        statusId = this.statusId,
        timeIn = this.timeIn,
        timeOut = this.timeOut,
        workHour = this.workHour,
        attendanceDate = this.attendanceDate,
        notes = this.notes
    )
}

/**
 * Extension function to convert AttendanceRequestModel (Domain model) to AttendanceRequest (DTO)
 */
fun AttendanceRequestModel.toDto(): AttendanceRequest {
    return AttendanceRequest(
        categoryId = this.categoryId,
        latitude = this.latitude,
        longitude = this.longitude,
        notes = this.notes,
        bookingId = this.bookingId
    )
}
