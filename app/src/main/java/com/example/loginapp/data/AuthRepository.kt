package com.example.loginapp.data

import com.example.loginapp.data.network.AuthApi
import com.example.loginapp.data.network.LoginRequest
import com.example.loginapp.data.network.RegisterRequest
import com.example.loginapp.data.network.RetrofitClient
import com.google.gson.Gson

/**
 * Single source of truth for authentication on the Android side.
 *
 * Replaces the old in-memory [UserRepository]. Every method is a suspend
 * function — call it from a coroutine scope (e.g. viewModelScope or
 * lifecycleScope) so network work never runs on the main thread.
 */
class AuthRepository(
    private val api: AuthApi = RetrofitClient.authApi
) {

    private val gson = Gson()

    // ── Email / password ──────────────────────────────────────────────────────

    /** Register a new account. Returns [AuthResult.Success] with token on 201. */
    suspend fun register(email: String, password: String): AuthResult {
        return safeCall {
            api.register(RegisterRequest(email.trim().lowercase(), password))
        }
    }

    /**
     * Log in with email and password.
     * Returns [AuthResult.Success] with token on 200.
     */
    suspend fun login(email: String, password: String): AuthResult {
        return safeCall {
            api.login(LoginRequest(email.trim().lowercase(), password))
        }
    }

    // ── Shared response handler ───────────────────────────────────────────────

    /**
     * Executes [call], maps a successful 2xx response to [AuthResult.Success],
     * and translates every failure (HTTP error, network error, parse error) to
     * [AuthResult.Error] with a human-readable message.
     */
    private suspend fun safeCall(
        call: suspend () -> retrofit2.Response<com.example.loginapp.data.network.AuthResponse>
    ): AuthResult {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    AuthResult.Success(
                        token  = body.token,
                        email  = body.email,
                        userId = body.userId
                    )
                } else {
                    AuthResult.Error("The server returned an empty response.")
                }
            } else {
                // Parse the {"error": "..."} JSON body from the Ktor backend.
                val errorJson = response.errorBody()?.string()
                val message = if (!errorJson.isNullOrBlank()) {
                    runCatching {
                        gson.fromJson(errorJson, com.example.loginapp.data.network.ErrorResponse::class.java).error
                    }.getOrDefault("Request failed (HTTP ${response.code()}).")
                } else {
                    "Request failed (HTTP ${response.code()})."
                }
                AuthResult.Error(message)
            }
        } catch (e: java.net.ConnectException) {
            AuthResult.Error("Cannot reach the server. Make sure the Ktor backend is running on port 8080.")
        } catch (e: java.net.SocketTimeoutException) {
            AuthResult.Error("The request timed out. Check your network connection.")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "An unexpected error occurred.")
        }
    }
}

// ── Result type ───────────────────────────────────────────────────────────────

/**
 * Replaces the old sealed interface from UserRepository.
 * [Success] carries the token, email, and userId returned by the server.
 */
sealed interface AuthResult {
    data class Success(
        val token:  String,
        val email:  String,
        val userId: String
    ) : AuthResult

    data class Error(val message: String) : AuthResult
}
