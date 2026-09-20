package com.example.loginapp.data

/**
 * A very simple in-memory user store.
 */

object UserRepository {

    /** Composite identity for a student within a school. */
    private data class UserKey(val studentId: String, val schoolId: String)

    private fun keyOf(studentId: String, schoolId: String) =
        UserKey(studentId.trim().lowercase(), schoolId.trim().lowercase())

    // (studentId, schoolId) -> password
    private val users = mutableMapOf(
        // A demo account you can log in with right away:
        // School id is the full campus name (lowercased by keyOf).
        UserKey("demo", "taguig city university") to "password123"
    )

    /** Returns true if an account already exists for this student/school pair. */
    fun userExists(studentId: String, schoolId: String): Boolean =
        users.containsKey(keyOf(studentId, schoolId))

    /** Registers a new user. Returns a Result describing success or failure. */
    fun register(studentId: String, schoolId: String, password: String): AuthResult {
        val key = keyOf(studentId, schoolId)
        if (users.containsKey(key)) {
            return AuthResult.Error("An account already exists for that Student ID at this school.")
        }
        users[key] = password
        return AuthResult.Success
    }

    /** Validates credentials. Returns a Result describing success or failure. */
    fun login(studentId: String, schoolId: String, password: String): AuthResult {
        val stored = users[keyOf(studentId, schoolId)]
            ?: return AuthResult.Error("No account found for that Student ID and School ID.")
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
