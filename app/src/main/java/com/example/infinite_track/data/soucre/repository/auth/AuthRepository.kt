package com.example.infinite_track.data.soucre.repository.auth

import android.util.Log
import com.example.infinite_track.data.soucre.local.preferences.UserPreference
import com.example.infinite_track.data.soucre.network.request.LoginRequest
import com.example.infinite_track.data.soucre.network.request.ProfileRequest
import com.example.infinite_track.data.soucre.network.response.ErrorResponse
import com.example.infinite_track.data.soucre.network.response.ProfileResponse
import com.example.infinite_track.data.soucre.network.response.UserResponse
import com.example.infinite_track.data.soucre.network.retrofit.ApiService
import com.example.infinite_track.domain.model.userdata.UserModel
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val pref: UserPreference,
    private val apiService: ApiService,
) {
    fun getUser(): Flow<UserModel> {
        return pref.getUser()
    }
    suspend fun saveUser(user: UserModel) {
        pref.saveUser(user)
    }

    suspend fun logout() {
        pref.logout()
    }
    suspend fun login(request: LoginRequest): Flow<UserResponse> {
        try {
            val response = apiService.login(request)

            Log.d("Response Viewmodel: ", "${response.profilePhoto}")

            val user = UserModel(
                email = response.email,
                token = response.token,
                userId = response.userId,
                userName = response.userName,
                userRole = response.userRole,
                address = response.address,
                annualBalance = response.annualBalance,
                annualUsed = response.annualUsed,
                division = response.division,
                greeting = response.greeting,
                nip_nim = response.nipNim,
                phone_number = response.phoneNumber,
                positionName = response.positionName,
                start_contract = response.startContract,
                end_contract = response.endContract,
                headprogramname = response.headprogramname,
                profilePhoto = response.profilePhoto
            )
            saveUser(user)
            return flow { emit(response) }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody?.message ?: "Unknown error from server"
            Log.e("AuthRepository", "HTTP Error: $errorMessage")
            throw Exception(errorMessage)
        } catch (e: IOException) {
            Log.e("AuthRepository", "Network Error: ${e.message}")
            throw Exception("Network error, please check your internet connection.")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Unknown Error: ${e.message}")
            throw Exception(e.message ?: "Unexpected error occurred.")
        }
    }
}