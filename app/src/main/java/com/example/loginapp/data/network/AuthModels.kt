package com.example.loginapp.data.network

import com.google.gson.annotations.SerializedName

// ── Requests ──────────────────────────────────────────────────────────────────

data class LoginRequest(
    @SerializedName("email")    val email:    String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("email")    val email:    String,
    @SerializedName("password") val password: String
)

data class SendOtpRequest(
    @SerializedName("email") val email: String
)

data class VerifyOtpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("code")  val code:  String
)

// ── Responses ─────────────────────────────────────────────────────────────────

data class AuthResponse(
    @SerializedName("token")  val token:  String,
    @SerializedName("email")  val email:  String,
    @SerializedName("userId") val userId: String,
    /** Backend may include role in future; defaults to "user" if absent. */
    @SerializedName("role")   val role:   String? = null
)

data class MessageResponse(
    @SerializedName("message") val message: String
)

data class ErrorResponse(
    @SerializedName("error") val error: String
)
