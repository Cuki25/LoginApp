package com.medkit.models

import kotlinx.serialization.Serializable

// ── Requests ─────────────────────────────────────────────────────────────────

@Serializable
data class RegisterRequest(
    val email:         String,
    val password:      String,
    val studentNumber: String = ""
)

@Serializable
data class LoginRequest(
    val email:    String,
    val password: String
)

@Serializable
data class SendOtpRequest(
    val email: String
)

@Serializable
data class VerifyOtpRequest(
    val email: String,
    val code:  String
)

// ── Responses ─────────────────────────────────────────────────────────────────

@Serializable
data class AuthResponse(
    val token:         String,
    val email:         String,
    val userId:        String,
    val role:          String  = "user",
    val studentNumber: String? = null
)

@Serializable
data class ErrorResponse(
    val error: String
)

@Serializable
data class MessageResponse(
    val message: String
)
