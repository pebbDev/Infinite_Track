package com.example.infinite_track.di

import android.content.Context
import android.util.Log
import com.example.infinite_track.data.soucre.local.preferences.LocalizationPreference
import com.example.infinite_track.data.soucre.local.preferences.UserPreference
import com.example.infinite_track.data.soucre.local.preferences.dataLanguage
import com.example.infinite_track.data.soucre.local.preferences.dataUserStore
import com.example.infinite_track.data.soucre.network.retrofit.ApiConfig
import com.example.infinite_track.data.soucre.network.retrofit.ApiService
import com.example.infinite_track.data.soucre.repository.Attendance.AttendanceHistoryRepository
import com.example.infinite_track.data.soucre.repository.Attendance.AttendanceOverviewRepository
import com.example.infinite_track.data.soucre.repository.Attendance.AttendanceRepository
import com.example.infinite_track.data.soucre.repository.auth.AuthRepository
import com.example.infinite_track.data.soucre.repository.contact.ContactsRepository
import com.example.infinite_track.data.soucre.repository.leave.LeaveRepository
import com.example.infinite_track.data.soucre.repository.profile.ProfileRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideUserPreference(@ApplicationContext context: Context): UserPreference {
        try {
            return UserPreference(context.dataUserStore)
        } catch (e: Exception) {
            Log.e("AppModule", "Error Providing UserPreference: ${e.message}", e)
            throw e
        }
    }

    @Provides
    fun providerMultiLanguage(@ApplicationContext context: Context): LocalizationPreference {
        try {
            return LocalizationPreference(context.dataLanguage)
        } catch (e: Exception) {
            Log.e("AppModule", "Error Providing HistoryRepository: ${e.message}", e)
            throw e
        }
    }

    @Provides
    fun provideApiConfig(): ApiConfig {
        try {
            return ApiConfig()
        } catch (e: Exception) {
            Log.e("AppModule", "Error Providing ApiConfig: ${e.message}", e)
            throw e
        }
    }

    @Provides
    fun provideApiService(@ApplicationContext context: Context): ApiService {
        return ApiConfig().getApiService(context)
    }

    @Provides
    fun provideAttendanceRepository(@ApplicationContext context: Context ): AttendanceRepository {
        val pref = provideUserPreference(context)
        val apiService = provideApiConfig().getApiService(context)

        try {
            return AttendanceRepository(
                apiService, pref
            )
        } catch (e: Exception) {
            Log.e("AppModule", "Error providing Provide AttendanceRepository: ${e.message}", e)
            throw e
        }
    }

    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    fun provideProfileRepository(@ApplicationContext context: Context ): ProfileRepository {
        val apiService = provideApiConfig().getApiService(context)
        val pref = provideUserPreference(context)

        try {
            return ProfileRepository(
                pref,apiService
            )
        } catch (e: Exception) {
            Log.e("AppModule", "Error providing ProvideAttendanceRepository: ${e.message}", e)
            throw e
        }
    }

    @Provides
    fun provideAuthRepository(@ApplicationContext context: Context): AuthRepository {
        val pref = provideUserPreference(context)
        val apiService = provideApiConfig().getApiService(context)

        try {
            return AuthRepository(
                pref, apiService
            )
        } catch (e: Exception) {
            Log.e("AppModule", "Error providing AuthRepository: ${e.message}", e)
            throw e
        }
    }

    @Provides
    fun providerHistoryRepository(@ApplicationContext context: Context): AttendanceHistoryRepository {
        val apiService = provideApiConfig().getApiService(context)
        val pref = provideUserPreference(context)
        try {
            return AttendanceHistoryRepository(apiService,pref)
        } catch (e: Exception) {
            Log.e("AppModule", "Error providing HistoryRepository: ${e.message}", e)
            throw e
        }
    }

    @Provides
    fun providerLeaveRepository(@ApplicationContext context: Context): LeaveRepository {
        val apiService = provideApiConfig().getApiService(context)
        try {
            return LeaveRepository(apiService)
        } catch (e: Exception) {
            Log.e("AppModule", "Error providing LeaveRepository: ${e.message}", e)
            throw e
        }
    }

    @Provides
    fun providerContactsRepository(@ApplicationContext context: Context): ContactsRepository {
        val apiService = provideApiConfig().getApiService(context)
        try {
            return ContactsRepository(apiService)
        } catch (e: Exception) {
            Log.e("AppModule", "Error providing Contact Repository: ${e.message}", e)
            throw e
        }
    }

    @Provides
    fun providerAttendanceOverviewRepository(@ApplicationContext context: Context): AttendanceOverviewRepository {
        val apiService = provideApiConfig().getApiService(context)
        val pref = provideUserPreference(context)
        try {
            return AttendanceOverviewRepository(apiService, pref)
        } catch (e: Exception) {
            Log.e("AppModule", "Error providing Attendance Overview Repository: ${e.message}", e)
            throw e
        }
    }
}
