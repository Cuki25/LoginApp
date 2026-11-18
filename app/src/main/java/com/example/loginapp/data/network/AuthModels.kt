package com.example.loginapp.data.network

import com.google.gson.annotations.SerializedName

// ── Requests ──────────────────────────────────────────────────────────────────

data class LoginRequest(
    @SerializedName("email")    val email:    String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("email")         val email:         String,
    @SerializedName("password")      val password:      String,
    @SerializedName("studentNumber") val studentNumber: String
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
    @SerializedName("token")         val token:         String,
    @SerializedName("email")         val email:         String,
    @SerializedName("userId")        val userId:        String,
    @SerializedName("role")          val role:          String? = null,
    @SerializedName("studentNumber") val studentNumber: String? = null
)

data class MessageResponse(
    @SerializedName("message") val message: String
)

data class ErrorResponse(
    @SerializedName("error")   val error:   String? = null,
    @SerializedName("message") val message: String? = null
)
