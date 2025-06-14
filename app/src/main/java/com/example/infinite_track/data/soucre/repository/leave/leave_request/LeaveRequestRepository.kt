package com.example.infinite_track.data.soucre.repository.leave.leave_request

import com.example.infinite_track.data.soucre.network.response.ErrorResponse
import com.example.infinite_track.data.soucre.network.response.LeaveRequestResponse
import com.example.infinite_track.data.soucre.network.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaveRequestRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun leaveRequest(
        name: RequestBody,
        headProgramName: RequestBody,
        division: RequestBody,
        startDate: RequestBody,
        endDate: RequestBody,
        leaveType: RequestBody,
        desc: RequestBody,
        phone: RequestBody,
        address: RequestBody,
        uploadImage: MultipartBody.Part
    ): Flow<LeaveRequestResponse> {

        return flow {
            try {
                val response = apiService.leaveRequest(
                    name = name,
                    headProgramName = headProgramName,
                    division = division,
                    startDate = startDate,
                    endDate = endDate,
                    leaveType = leaveType,
                    desc = desc,
                    phone = phone,
                    address = address,
                    uploadImage = uploadImage
                )
                emit(response)
            } catch (e: retrofit2.HttpException) {
                handleHttpException(e)
            }
        }
    }

    private fun handleHttpException(e: retrofit2.HttpException): Nothing {
        val jsonInString = e.response()?.errorBody()?.string()
        val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
        val errorMessage = errorBody?.message ?: "Unknown Error"
        throw Exception(errorMessage)
    }


}