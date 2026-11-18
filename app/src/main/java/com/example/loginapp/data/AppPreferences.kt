package com.example.loginapp.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Persistent user preferences backed by SharedPreferences.
 * Exposes StateFlow so Compose can observe changes reactively.
 */
class AppPreferences(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ── Dark mode ─────────────────────────────────────────────────────────────

    private val _darkMode = MutableStateFlow(prefs.getBoolean(KEY_DARK_MODE, false))
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
        _darkMode.value = enabled
    }

    // ── Read aloud (TTS) ──────────────────────────────────────────────────────

    private val _readAloud = MutableStateFlow(prefs.getBoolean(KEY_READ_ALOUD, false))
    val readAloud: StateFlow<Boolean> = _readAloud.asStateFlow()

    fun setReadAloud(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_READ_ALOUD, enabled).apply()
        _readAloud.value = enabled
    }

    companion object {
        private const val PREFS_NAME    = "medkit_prefs"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_READ_ALOUD = "read_aloud"
    }
}
