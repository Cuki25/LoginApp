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
    val authSuccess:  AuthSuccessData? = null,
    val otpSent:      Boolean = false,
    val otpVerified:  Boolean = false,
    val pendingEmail: String  = ""
)

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ── Login ─────────────────────────────────────────────────────────────────

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

    /** Step 1: send OTP to [email]. */
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

    /** Step 2: verify the 6-digit OTP the user entered. */
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
     * Step 3: create the account after OTP is verified.
     * [studentNumber] is validated on the server as well but the UI
     * pre-validates with the same regex before calling this.
     */
    fun completeRegistration(email: String, password: String, studentNumber: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = repo.register(email, password, studentNumber)) {
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

    fun clearAuthSuccess() = _uiState.update { it.copy(authSuccess = null) }
    fun clearOtpSent()     = _uiState.update { it.copy(otpSent = false) }
    fun clearError()       = _uiState.update { it.copy(errorMessage = null) }
}
