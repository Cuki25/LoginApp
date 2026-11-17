package com.example.loginapp.data

import com.example.loginapp.data.network.AuthApi
import com.example.loginapp.data.network.AuthResponse
import com.example.loginapp.data.network.ErrorResponse
import com.example.loginapp.data.network.LoginRequest
import com.example.loginapp.data.network.MessageResponse
import com.example.loginapp.data.network.RegisterRequest
import com.example.loginapp.data.network.RetrofitClient
import com.example.loginapp.data.network.SendOtpRequest
import com.example.loginapp.data.network.VerifyOtpRequest
import com.google.gson.Gson
import retrofit2.Response

/**
 * Single source of truth for all authentication network calls.
 * Every method is a suspend function — call from a ViewModel or coroutine scope.
 */
class AuthRepository(
    private val api: AuthApi = RetrofitClient.authApi
) {
    private val gson = Gson()

    // ── Email / password ──────────────────────────────────────────────────────

    suspend fun register(email: String, password: String): AuthResult<AuthSuccessData> =
        safeAuthCall { api.register(RegisterRequest(email.trim().lowercase(), password)) }

    suspend fun login(email: String, password: String): AuthResult<AuthSuccessData> =
        safeAuthCall { api.login(LoginRequest(email.trim().lowercase(), password)) }

    // ── OTP ───────────────────────────────────────────────────────────────────

    /** Tells the backend to generate and email a 6-digit OTP to [email]. */
    suspend fun sendOtp(email: String): AuthResult<String> =
        safeMessageCall { api.sendOtp(SendOtpRequest(email.trim().lowercase())) }

    /** Validates [code] against the backend for [email]. */
    suspend fun verifyOtp(email: String, code: String): AuthResult<String> =
        safeMessageCall { api.verifyOtp(VerifyOtpRequest(email.trim().lowercase(), code.trim())) }

    // ── Response mappers ──────────────────────────────────────────────────────

    private suspend fun safeAuthCall(
        call: suspend () -> Response<AuthResponse>
    ): AuthResult<AuthSuccessData> = try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                AuthResult.Success(
                    AuthSuccessData(
                        token  = body.token,
                        email  = body.email,
                        userId = body.userId,
                        role   = body.role ?: "user"
                    )
                )
            } else AuthResult.Error("The server returned an empty response.")
        } else {
            AuthResult.Error(parseError(response.errorBody()?.string(), response.code()))
        }
    } catch (e: java.net.ConnectException) {
        AuthResult.Error("Cannot reach the server. Make sure the Ktor backend is running on port 8080.")
    } catch (e: java.net.SocketTimeoutException) {
        AuthResult.Error("The request timed out. Check your network connection.")
    } catch (e: Exception) {
        AuthResult.Error(e.message ?: "An unexpected error occurred.")
    }

    private suspend fun safeMessageCall(
        call: suspend () -> Response<MessageResponse>
    ): AuthResult<String> = try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            AuthResult.Success(body?.message ?: "Success.")
        } else {
            AuthResult.Error(parseError(response.errorBody()?.string(), response.code()))
        }
    } catch (e: java.net.ConnectException) {
        AuthResult.Error("Cannot reach the server. Make sure the Ktor backend is running on port 8080.")
    } catch (e: java.net.SocketTimeoutException) {
        AuthResult.Error("The request timed out. Check your network connection.")
    } catch (e: Exception) {
        AuthResult.Error(e.message ?: "An unexpected error occurred.")
    }

    private fun parseError(json: String?, httpCode: Int): String =
        if (!json.isNullOrBlank()) {
            runCatching {
                gson.fromJson(json, ErrorResponse::class.java).error
            }.getOrDefault("Request failed (HTTP $httpCode).")
        } else {
            "Request failed (HTTP $httpCode)."
        }
}

// ── Result types ──────────────────────────────────────────────────────────────

sealed interface AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>
    data class Error(val message: String) : AuthResult<Nothing>
}

data class AuthSuccessData(
    val token:  String,
    val email:  String,
    val userId: String,
    val role:   String   // "user" | "admin" | "super_admin"
)
