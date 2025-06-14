package com.example.infinite_track.data.soucre.network.retrofit

import com.example.infinite_track.data.soucre.network.request.AttendanceRequest
import com.example.infinite_track.data.soucre.network.request.LoginRequest
import com.example.infinite_track.data.soucre.network.request.ProfileRequest
import com.example.infinite_track.data.soucre.network.response.AttendanceHistoryResponseItem
import com.example.infinite_track.data.soucre.network.response.AttendanceOverviewResponse
import com.example.infinite_track.data.soucre.network.response.AttendanceResponse
import com.example.infinite_track.data.soucre.network.response.ContactsResponse
import com.example.infinite_track.data.soucre.network.response.LeaveRequestResponse
import com.example.infinite_track.data.soucre.network.response.LeaveHistoryResponse
import com.example.infinite_track.data.soucre.network.response.MyLeaveResponse
import com.example.infinite_track.data.soucre.network.response.ProfileResponse
import com.example.infinite_track.data.soucre.network.response.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import javax.inject.Singleton

@Singleton
interface ApiService {
    @POST("/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): UserResponse

    @Headers("Content-Type: application/json")
    @POST("/attendance/users")
    suspend fun workFromOffice(
        @Body attendanceRequest: AttendanceRequest,
        @Header("Authorization") token: String
    ): AttendanceResponse

    @GET("/users/attendance/{userId}")
    suspend fun getAllAttendance(@Path("userId") userId: Int): List<AttendanceHistoryResponseItem>

    @Multipart
    @POST("/attendance/users")
    suspend fun workFromHome(
        @Header("Authorization") token: String,
        @Part("attendance_category") attendanceCategory: RequestBody,
        @Part("action") action: RequestBody,
        @Part("notes") notes: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part uploadImage: MultipartBody.Part
    ): AttendanceResponse

    @Multipart
    @POST("/leave/users")
    suspend fun leaveRequest(
        @Part("name") name: RequestBody,
        @Part("headProgramName") headProgramName: RequestBody,
        @Part("division") division: RequestBody,
        @Part("start_date") startDate: RequestBody,
        @Part("end_date") endDate: RequestBody,
        @Part("leavetype") leaveType: RequestBody,
        @Part("description") desc: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("address") address: RequestBody,
        @Part uploadImage: MultipartBody.Part
    ): LeaveRequestResponse

    @PUT("/users/{id}")
    suspend fun updateUser(
        @Path("id") userId: Int,
        @Body profileRequest: ProfileRequest
    ): Response<ProfileResponse>

    @GET("/leave/history")
    suspend fun getAllLeave(): LeaveHistoryResponse

    @GET("/contacts")
    suspend fun getContacts(): ContactsResponse

    @GET("/attendance/users/overview")
    suspend fun getAttendanceOverview(
        @Header("Authorization") token: String
    ): AttendanceOverviewResponse
}