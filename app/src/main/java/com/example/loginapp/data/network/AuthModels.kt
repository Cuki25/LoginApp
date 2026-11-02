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

// ── Responses ─────────────────────────────────────────────────────────────────

data class AuthResponse(
    @SerializedName("token")   val token:  String,
    @SerializedName("email")   val email:  String,
    @SerializedName("userId")  val userId: String
)

data class ErrorResponse(
    @SerializedName("error") val error: String
)
