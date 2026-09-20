package com.example.loginapp.data

import android.content.Context

class SessionManager(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Persist a successful login. Call this right after authentication succeeds. */
    fun saveLoginSession(studentId: String, schoolId: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_STUDENT_ID, studentId)
            .putString(KEY_SCHOOL_ID, schoolId)
            .apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getStudentId(): String? = prefs.getString(KEY_STUDENT_ID, null)

    fun getSchoolId(): String? = prefs.getString(KEY_SCHOOL_ID, null)

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "medkit_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_STUDENT_ID = "student_id"
        private const val KEY_SCHOOL_ID = "school_id"
    }
}
