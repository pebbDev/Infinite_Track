package com.example.infinite_track.data.soucre.repository.Attendance

import android.util.Log
import com.example.infinite_track.data.soucre.local.preferences.UserPreference
import com.example.infinite_track.data.soucre.network.request.AttendanceRequest
import com.example.infinite_track.data.soucre.network.response.AttendanceResponse
import com.example.infinite_track.data.soucre.network.response.ErrorResponse
import com.example.infinite_track.data.soucre.network.retrofit.ApiService
import com.example.infinite_track.domain.model.AttendanceState
import com.example.infinite_track.utils.calculateDistance
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepository @Inject constructor(
    private val apiService: ApiService,
    private val pref: UserPreference,
) {

    companion object {
        private const val OFFICE_LATITUDE = 1.1851
        private const val OFFICE_LONGITUDE = 104.1019
        private const val ALLOWED_RADIUS = 5000f
    }

    fun isWithinGeofence(userLat: Float, userLon: Float): Boolean {
        Log.d("Geofence", "Latitude 1 (Office): $OFFICE_LATITUDE, Longitude 1 (Office): $OFFICE_LONGITUDE")
        Log.d("Geofence", "Latitude 2 (User): $userLat, Longitude 2 (User): $userLon")
        val distance = calculateDistance(OFFICE_LATITUDE.toFloat(), OFFICE_LONGITUDE.toFloat(), userLat, userLon)
        Log.d("Geofence", "Calculated Distance: $distance meters")

        val allowedRadius = 500f
        if (distance > allowedRadius) {
            Log.d("Geofence", "User is out of range. Distance: $distance meters, Allowed radius: $allowedRadius meters")
            throw Exception("Please move to the designated location!")
        }
        return distance <= ALLOWED_RADIUS
    }


    suspend fun workFromOffice(request: AttendanceRequest): Flow<AttendanceResponse> {
        val token = pref.getUser().firstOrNull()?.token
            ?: throw Exception("Token tidak ditemukan. Silakan login kembali.")

        return flow {
            if (!isWithinGeofence(request.latitude!!.toFloat(), request.longitude!!.toFloat())) {
                    Log.d("Geofence", "User's Location: (${request.latitude}, ${request.longitude}) is out of range")
                throw Exception("Please move to the designated location!")
            }

            try {
                val response = apiService.workFromOffice(request, "Bearer $token")
                emit(response)
            } catch (e: retrofit2.HttpException) {
                handleHttpException(e)
            }
        }.take(1)
    }

    suspend fun workFromHome(
        attendanceCategory: RequestBody,
        action: RequestBody,
        notes: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody,
        uploadImage: MultipartBody.Part
    ): Flow<AttendanceResponse> {
        val token = pref.getUser().firstOrNull()?.token
            ?: throw Exception("Token tidak ditemukan. Silakan login kembali.")

        return flow {
            try {
                val response = apiService.workFromHome(
                    attendanceCategory = attendanceCategory,
                    action = action,
                    notes = notes,
                    latitude = latitude,
                    longitude = longitude,
                    uploadImage = uploadImage,
                    token = "Bearer $token"
                )
                emit(response)
            } catch (e: retrofit2.HttpException) {
                handleHttpException(e)
            }
        }.take(1)
    }

    private fun handleHttpException(e: retrofit2.HttpException): Nothing {
        val jsonInString = e.response()?.errorBody()?.string()
        val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
        val errorMessage = errorBody?.message ?: "Unknown Error"
        throw Exception(errorMessage)
    }

    suspend fun resetAttendanceIfNeeded() {
        val lastCheckoutTime = pref.getAttendanceState().firstOrNull()?.lastCheckoutTime ?: return
        val currentTime = System.currentTimeMillis()
        if (isNextDayAfter7AM(lastCheckoutTime, currentTime)) {
            pref.saveAttendanceState(false, false, 0L)
        }
    }

    private fun isNextDayAfter7AM(lastTime: Long, currentTime: Long): Boolean {
        val lastCheckoutDate = Calendar.getInstance().apply { timeInMillis = lastTime }
        val currentDate = Calendar.getInstance().apply { timeInMillis = currentTime }
        return currentDate.get(Calendar.DAY_OF_YEAR) > lastCheckoutDate.get(Calendar.DAY_OF_YEAR) &&
                currentDate.get(Calendar.HOUR_OF_DAY) >= 7
    }

    fun getAttendanceState(): Flow<AttendanceState> {
        return pref.getAttendanceState()
    }

    suspend fun saveAttendanceState(isAttend: Boolean, isCheckedOut: Boolean, lastCheckoutTime: Long) {
        pref.saveAttendanceState(isAttend, isCheckedOut, lastCheckoutTime)
    }
}
