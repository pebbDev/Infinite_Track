package com.example.infinite_track.data.soucre.network.retrofit

import android.content.Context
import android.os.Build
import com.example.infinite_track.data.soucre.local.preferences.UserPreference
import com.example.infinite_track.data.soucre.local.preferences.dataUserStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiConfig @Inject constructor() {

    fun getApiService(context: Context): ApiService {

        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val authInterceptor = Interceptor { chain ->
            val token = getTokenFromContext(context)
            val req = chain.request()

            val request = req.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }
}

private fun getTokenFromContext(context: Context): String {
    val pref = UserPreference(context.dataUserStore)
    val user = runBlocking {
        pref.getUser().first()
    }
    return user.token ?: ""
}

private fun isEmulator(): Boolean {
    return Build.FINGERPRINT.contains("generic") || Build.FINGERPRINT.contains("emulator")
}

private val baseUrl: String
    get() = if (isEmulator()) {
        "http://10.0.2.2:3000"
    } else {
        "http://192.168.27.197:3000"
    }