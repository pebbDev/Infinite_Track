package com.example.infinite_track.di

import android.os.Build
import com.example.infinite_track.data.soucre.local.preferences.UserPreference
import com.example.infinite_track.data.soucre.network.retrofit.ApiService
import com.example.infinite_track.data.soucre.network.retrofit.MapboxApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // 1. Menyediakan Interceptor untuk Logging
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    // 2. Menyediakan Interceptor untuk Otentikasi
    @Provides
    @Singleton
    fun provideAuthInterceptor(userPreference: UserPreference): Interceptor {
        return Interceptor { chain ->
            // Mengambil token secara sinkron dari DataStore
            val token = runBlocking { userPreference.getAuthToken().first() }
            val requestBuilder = chain.request().newBuilder()

            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(requestBuilder.build())
        }
    }

    // 3. Menyediakan OkHttpClient dengan timeout yang lebih panjang
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS) // Increased timeout
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // 4. Menyediakan Retrofit untuk Backend API (yang sudah ada)
    @Provides
    @Singleton
    @Named("backend")
    fun provideBackendRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl) // Base URL backend lokal
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    // 5. Menyediakan Retrofit untuk Mapbox API (BARU)
    @Provides
    @Singleton
    @Named("mapbox")
    fun provideMapboxRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.mapbox.com/") // Base URL Mapbox langsung
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()
            )
            .build()
    }

    // 6. ApiService untuk Backend (yang sudah ada)
    @Provides
    @Singleton
    fun provideApiService(@Named("backend") retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    // 7. MapboxApiService untuk Mapbox API (BARU)
    @Provides
    @Singleton
    fun provideMapboxApiService(@Named("mapbox") retrofit: Retrofit): MapboxApiService {
        return retrofit.create(MapboxApiService::class.java)
    }

    // Provide Gson instance for dependency injection
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    // Helper method for reliable emulator detection that worked for you
    private fun isEmulator(): Boolean {
        return Build.FINGERPRINT.contains("generic") || Build.FINGERPRINT.contains("emulator")
    }

    // Property that returns the appropriate baseUrl based on device type
    private val baseUrl: String
        get() = if (isEmulator()) {
            "http://10.0.2.2:3005/"
        } else {
            "http://192.168.1.11:3005/"
        }
}