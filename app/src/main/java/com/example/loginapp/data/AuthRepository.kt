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

    suspend fun register(
        email:         String,
        password:      String,
        studentNumber: String
    ): AuthResult<AuthSuccessData> =
        safeAuthCall {
            api.register(
                RegisterRequest(
                    email         = email.trim().lowercase(),
                    password      = password,
                    studentNumber = studentNumber.trim()
                )
            )
        }

    suspend fun login(email: String, password: String): AuthResult<AuthSuccessData> =
        safeAuthCall { api.login(LoginRequest(email.trim().lowercase(), password)) }

    // ── OTP ───────────────────────────────────────────────────────────────────

    suspend fun sendOtp(email: String): AuthResult<String> =
        safeMessageCall { api.sendOtp(SendOtpRequest(email.trim().lowercase())) }

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
                        token         = body.token,
                        email         = body.email,
                        userId        = body.userId,
                        role          = body.role ?: "user",
                        studentNumber = body.studentNumber
                    )
                )
            } else AuthResult.Error("The server returned an empty response.")
        } else {
            AuthResult.Error(parseError(response.errorBody()?.string(), response.code()))
        }
    } catch (e: java.net.ConnectException) {
        AuthResult.Error("Cannot reach the server. Check your internet connection.")
    } catch (e: java.net.SocketTimeoutException) {
        AuthResult.Error("The request timed out. The server may be starting up — please try again.")
    } catch (e: Exception) {
        AuthResult.Error(e.message?.takeIf { it.isNotBlank() } ?: "An unexpected error occurred.")
    }

    private suspend fun safeMessageCall(
        call: suspend () -> Response<MessageResponse>
    ): AuthResult<String> = try {
        val response = call()
        if (response.isSuccessful) {
            AuthResult.Success(response.body()?.message ?: "Success.")
        } else {
            AuthResult.Error(parseError(response.errorBody()?.string(), response.code()))
        }
    } catch (e: java.net.ConnectException) {
        AuthResult.Error("Cannot reach the server. Check your internet connection.")
    } catch (e: java.net.SocketTimeoutException) {
        AuthResult.Error("The request timed out. The server may be starting up — please try again.")
    } catch (e: Exception) {
        AuthResult.Error(e.message?.takeIf { it.isNotBlank() } ?: "An unexpected error occurred.")
    }

    /**
     * Tries to parse the backend JSON error body, checking both "error" and
     * "message" fields (the backend uses "error" for failures and "message"
     * for successes). Falls back to a generic HTTP status description.
     */
    private fun parseError(json: String?, httpCode: Int): String {
        if (!json.isNullOrBlank()) {
            runCatching {
                val err = gson.fromJson(json, ErrorResponse::class.java)
                val text = err.error?.takeIf { it.isNotBlank() }
                    ?: err.message?.takeIf { it.isNotBlank() }
                if (!text.isNullOrBlank()) return text
            }
        }
        return "Request failed (HTTP $httpCode)."
    }
}

// ── Result types ──────────────────────────────────────────────────────────────

sealed interface AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>
    data class Error(val message: String) : AuthResult<Nothing>
}

data class AuthSuccessData(
    val token:         String,
    val email:         String,
    val userId:        String,
    val role:          String,
    val studentNumber: String? = null
)
