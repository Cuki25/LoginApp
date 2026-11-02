package com.example.loginapp.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit interface for the two auth endpoints on the Ktor backend.
 * All methods are suspend functions so they can be called from a coroutine
 * or ViewModel without blocking the main thread.
 */
interface AuthApi {

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>
}
