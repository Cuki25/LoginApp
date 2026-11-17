package com.example.loginapp.data

import android.content.Context

/**
 * Persists the authenticated session across app restarts using SharedPreferences.
 *
 * Stored keys:
 *  - is_logged_in  (Boolean)
 *  - jwt_token     (String)  — Bearer token sent with every API request
 *  - user_email    (String)  — shown in the UI, used for display only
 *  - user_id       (String)  — UUID from the Users table
 *  - auth_provider (String)  — "local" | "google" | "microsoft"
 *  - user_role     (String)  — "user" | "admin" | "super_admin"
 */
class SessionManager(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ── Write ─────────────────────────────────────────────────────────────────

    /** Call this immediately after a successful login or registration. */
    fun saveSession(
        token:    String,
        email:    String,
        userId:   String,
        provider: String = "local",
        role:     String = "user"
    ) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_TOKEN,    token)
            .putString(KEY_EMAIL,    email)
            .putString(KEY_USER_ID,  userId)
            .putString(KEY_PROVIDER, provider)
            .putString(KEY_ROLE,     role)
            .apply()
    }

    /** Wipe the session on logout or token expiry. */
    fun clearSession() {
        prefs.edit().clear().apply()
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    fun isLoggedIn(): Boolean  = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    fun getToken():    String? = prefs.getString(KEY_TOKEN,    null)
    fun getEmail():    String? = prefs.getString(KEY_EMAIL,    null)
    fun getUserId():   String? = prefs.getString(KEY_USER_ID,  null)
    fun getProvider(): String? = prefs.getString(KEY_PROVIDER, null)
    fun getRole():     String  = prefs.getString(KEY_ROLE,     null) ?: "user"

    /** Convenience: returns the Authorization header value for Retrofit. */
    fun getBearerHeader(): String? = getToken()?.let { "Bearer $it" }

    companion object {
        private const val PREFS_NAME       = "medkit_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_TOKEN        = "jwt_token"
        private const val KEY_EMAIL        = "user_email"
        private const val KEY_USER_ID      = "user_id"
        private const val KEY_PROVIDER     = "auth_provider"
        private const val KEY_ROLE         = "user_role"
    }
}
