package com.example.infinite_track.domain.model

data class AhistoryModel(
    val id: Int,
    val date : String,
    val monthYear: String,
    val checkIn : String,
    val checkOut : String,
    val totalCourse : String,
)
