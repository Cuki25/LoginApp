package com.example.loginapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginapp.data.AuthRepository
import com.example.loginapp.data.AuthResult
import com.example.loginapp.data.AuthSuccessData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ── UI State ──────────────────────────────────────────────────────────────────

data class AuthUiState(
    val isLoading:    Boolean = false,
    val errorMessage: String? = null,
    /** Set when a login/register call succeeds — cleared on navigation. */
    val authSuccess:  AuthSuccessData? = null,
    /** Set when send-otp succeeds — signals the UI to advance to the OTP screen. */
    val otpSent:      Boolean = false,
    /** Set when verify-otp succeeds — signals the UI to call /auth/register. */
    val otpVerified:  Boolean = false,
    /** The email being registered (carried from RegisterScreen to OtpScreen). */
    val pendingEmail: String  = ""
)

/**
 * Shared ViewModel for LoginScreen, RegisterScreen, and OtpVerificationScreen.
 *
 * Survives configuration changes (rotation) because it lives in the Activity's
 * ViewModelStore. All UI events are channelled through [StateFlow]-backed state.
 */
class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ── Login ─────────────────────────────────────────────────────────────────

    /**
     * Called by [LoginScreen] on "Log in" press.
     * On success, [AuthUiState.authSuccess] is populated — the caller navigates
     * to the role-appropriate dashboard then calls [clearAuthSuccess].
     */
    fun login(email: String, password: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = repo.login(email, password)) {
                is AuthResult.Success -> _uiState.update {
                    it.copy(isLoading = false, authSuccess = result.data)
                }
                is AuthResult.Error   -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    // ── Registration ──────────────────────────────────────────────────────────

    /**
     * Step 1 of registration: sends an OTP to [email].
     * On success, [AuthUiState.otpSent] becomes true and [AuthUiState.pendingEmail]
     * is set so the OTP screen knows which address was used.
     */
    fun sendOtp(email: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, otpSent = false) }
        viewModelScope.launch {
            when (val result = repo.sendOtp(email)) {
                is AuthResult.Success -> _uiState.update {
                    it.copy(isLoading = false, otpSent = true, pendingEmail = email)
                }
                is AuthResult.Error   -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    /**
     * Step 2: verifies the OTP code the user entered.
     * On success, [AuthUiState.otpVerified] becomes true so the OTP screen can
     * immediately call [completeRegistration].
     */
    fun verifyOtp(email: String, code: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, otpVerified = false) }
        viewModelScope.launch {
            when (val result = repo.verifyOtp(email, code)) {
                is AuthResult.Success -> _uiState.update {
                    it.copy(isLoading = false, otpVerified = true)
                }
                is AuthResult.Error   -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    /**
     * Step 3: after OTP is verified, creates the actual account.
     * The backend's /auth/register will return 403 unless the email was verified.
     * On success, [AuthUiState.authSuccess] is populated.
     */
    fun completeRegistration(email: String, password: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = repo.register(email, password)) {
                is AuthResult.Success -> _uiState.update {
                    it.copy(isLoading = false, authSuccess = result.data, otpVerified = false)
                }
                is AuthResult.Error   -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    // ── State reset helpers ───────────────────────────────────────────────────

    /** Call after reading [AuthUiState.authSuccess] and navigating away. */
    fun clearAuthSuccess() = _uiState.update { it.copy(authSuccess = null) }

    /** Call after reading [AuthUiState.otpSent] and navigating to OTP screen. */
    fun clearOtpSent() = _uiState.update { it.copy(otpSent = false) }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
}
