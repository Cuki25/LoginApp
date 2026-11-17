package com.example.loginapp.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit interface mapping all auth endpoints on the Ktor backend.
 * All methods are suspend functions — call from a coroutine or ViewModel.
 */
interface AuthApi {

    // ── Email / password ──────────────────────────────────────────────────────

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    // ── OTP email verification ────────────────────────────────────────────────

    /** Triggers the server to generate and email a 6-digit OTP. */
    @POST("auth/send-otp")
    suspend fun sendOtp(@Body body: SendOtpRequest): Response<MessageResponse>

    /** Validates the 6-digit code the user entered. */
    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body body: VerifyOtpRequest): Response<MessageResponse>
}
