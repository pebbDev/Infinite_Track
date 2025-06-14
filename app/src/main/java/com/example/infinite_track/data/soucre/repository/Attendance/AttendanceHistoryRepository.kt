package com.example.infinite_track.data.soucre.repository.Attendance

import android.util.Log
import com.example.infinite_track.data.soucre.local.preferences.UserPreference
import com.example.infinite_track.data.soucre.network.response.AttendanceHistoryResponseItem
import com.example.infinite_track.data.soucre.network.response.AttendanceOverviewResponse
import com.example.infinite_track.data.soucre.network.response.ErrorResponse
import com.example.infinite_track.data.soucre.network.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceHistoryRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
) {
    suspend fun getAllAttendanceRepository(): Flow<List<AttendanceHistoryResponseItem>> {
        val userId = userPreference.getUser().firstOrNull()?.userId
            ?: throw Exception("User ID tidak ditemukan")

        return flow {
            try {
                val response = apiService.getAllAttendance(userId)
                Log.d("OverviewRepository", "Response: $response")  // Tambahkan log di sini
                emit(response)
            } catch (e: retrofit2.HttpException) {
                e.printStackTrace()
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody?.message ?: "Unknown Error"
                Log.e("AttendanceHistoryRepository", "Error: $errorMessage")
                throw Exception(errorMessage)
            }
        }
    }

    suspend fun getAllOverviewRepository(): Flow<AttendanceOverviewResponse> {
        val token = userPreference.getUser().firstOrNull()?.token
            ?: throw Exception("Token tidak ditemukan. Silakan login kembali.")

        return flow {
            try {
                val response = apiService.getAttendanceOverview("Bearer $token")
                emit(response)
            } catch (e: retrofit2.HttpException) {
                e.printStackTrace()
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody?.message ?: "Unknown Error"
                Log.e("OverviewRepository", "Error: $errorMessage")
                throw Exception(errorMessage)
            }
        }
    }
}
