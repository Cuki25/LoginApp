package com.example.loginapp.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton Retrofit instance.
 *
 * BASE_URL points to the Ktor server:
 *  - 10.0.2.2 is the Android emulator's alias for localhost on your dev machine.
 *  - Change to your LAN IP (e.g. 192.168.1.x) when testing on a physical device.
 *  - Change to your production domain before release.
 */
object RetrofitClient {

    // ── Change this when moving to production ────────────────────────────────
    // localhost:8080 is reached on a physical device via:
    //   adb reverse tcp:8080 tcp:8080
    private const val BASE_URL = "http://localhost:8080/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // BODY logs full request/response in Logcat — useful for development.
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
}
