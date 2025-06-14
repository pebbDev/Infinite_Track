package com.example.infinite_track.data.soucre.repository.Attendance

import android.util.Log
import com.example.infinite_track.data.soucre.local.preferences.UserPreference
import com.example.infinite_track.data.soucre.network.response.AttendanceOverviewResponse
import com.example.infinite_track.data.soucre.network.response.ErrorResponse
import com.example.infinite_track.data.soucre.network.response.LeaveHistoryResponse
import com.example.infinite_track.data.soucre.network.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceOverviewRepository @Inject constructor(
    private val apiService: ApiService,
    private val pref: UserPreference
) {
    suspend fun getAttendanceOverview(): Flow<AttendanceOverviewResponse> {
        val token = pref.getUser().firstOrNull()?.token
            ?: throw Exception("Token tidak ditemukan. Silakan login kembali.")

        try {
            val response = apiService.getAttendanceOverview("Bearer $token")
            return flow { emit(response) }
        } catch (e: retrofit2.HttpException) {
            e.printStackTrace()
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody?.message ?: "Unknown Error"
            Log.d("LeaveRepository", "getAllLeaveHistoryHome: $errorMessage")
            throw e
        }
    }
}