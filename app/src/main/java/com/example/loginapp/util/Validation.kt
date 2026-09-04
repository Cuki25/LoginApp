package com.example.loginapp.util

/** Small validation helpers shared by the login and sign-up screens. */
object Validation {

    fun usernameError(username: String): String? = when {
        username.isBlank() -> "Username is required."
        username.trim().length < 3 -> "Username must be at least 3 characters."
        else -> null
    }

    fun passwordError(password: String): String? = when {
        password.isBlank() -> "Password is required."
        password.length < 6 -> "Password must be at least 6 characters."
        else -> null
    }

    fun confirmPasswordError(password: String, confirm: String): String? = when {
        confirm.isBlank() -> "Please confirm your password."
        confirm != password -> "Passwords do not match."
        else -> null
    }
}
