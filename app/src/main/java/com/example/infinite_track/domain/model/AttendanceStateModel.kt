package com.example.infinite_track.domain.model

data class AttendanceState(
    val isAttend: Boolean,
    val isCheckedOut: Boolean,
    val lastCheckoutTime: Long
)
