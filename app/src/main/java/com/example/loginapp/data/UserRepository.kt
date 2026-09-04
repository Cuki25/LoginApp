package com.example.loginapp.data

/**
 * A very simple in-memory user store.
 *
 * This keeps the example self-contained (no database or network needed).
 * For a real app you would replace this with a backend call, Room database,
 * DataStore, or a service like Firebase Authentication.
 */
object UserRepository {

    // username -> password
    private val users = mutableMapOf(
        // A demo account you can log in with right away:
        "demo" to "password123"
    )

    /** Returns true if the username already exists. */
    fun userExists(username: String): Boolean =
        users.containsKey(username.trim().lowercase())

    /** Registers a new user. Returns a Result describing success or failure. */
    fun register(username: String, password: String): AuthResult {
        val key = username.trim().lowercase()
        if (userExists(key)) {
            return AuthResult.Error("That username is already taken.")
        }
        users[key] = password
        return AuthResult.Success
    }

    /** Validates credentials. Returns a Result describing success or failure. */
    fun login(username: String, password: String): AuthResult {
        val key = username.trim().lowercase()
        val stored = users[key] ?: return AuthResult.Error("No account found with that username.")
        return if (stored == password) {
            AuthResult.Success
        } else {
            AuthResult.Error("Incorrect password. Please try again.")
        }
    }
}

sealed interface AuthResult {
    data object Success : AuthResult
    data class Error(val message: String) : AuthResult
}
