package com.example.loginapp.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit instance pointed at the Render-hosted Ktor backend.
 *
 * The 60-second timeouts give Render's free tier enough time to wake from
 * its cold-sleep before the request is abandoned.
 */
object RetrofitClient {

    private const val BASE_URL = "https://medkit-backend.onrender.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // BODY logs full request/response in Logcat — useful during development.
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
}
