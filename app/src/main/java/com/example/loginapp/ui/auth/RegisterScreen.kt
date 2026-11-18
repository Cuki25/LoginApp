package com.example.loginapp.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.components.BrandMark
import com.example.loginapp.ui.components.MedKitButton
import com.example.loginapp.ui.components.MedKitTextField
import com.example.loginapp.ui.theme.MedKitBackground
import com.example.loginapp.ui.theme.MedKitEmergencyRed
import com.example.loginapp.ui.theme.MedKitInkFaint
import com.example.loginapp.ui.theme.MedKitInkMain
import com.example.loginapp.ui.theme.MedKitInkSoft

/** STI student number format: 02000 followed by exactly 6 digits. */
private val STUDENT_NUMBER_RE = Regex("^02000\\d{6}$")

/** Institution is fixed for this deployment. */
private const val INSTITUTION = "STI College Global City"

@Composable
fun RegisterScreen(
    viewModel:      AuthViewModel,
    /**
     * Called when the OTP was successfully dispatched.
     * Carries email, password, and studentNumber to the OTP screen.
     */
    onOtpSent:      (email: String, password: String, studentNumber: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var fullName        by remember { mutableStateOf("") }
    var studentNumber   by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var fullNameError     by remember { mutableStateOf<String?>(null) }
    var studentNumError   by remember { mutableStateOf<String?>(null) }
    var emailError        by remember { mutableStateOf<String?>(null) }
    var passwordError     by remember { mutableStateOf<String?>(null) }
    var confirmError      by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.otpSent) {
        if (uiState.otpSent) {
            viewModel.clearOtpSent()
            onOtpSent(email.trim().lowercase(), password, studentNumber.trim())
        }
    }

    fun validate(): Boolean {
        fullNameError = if (fullName.isBlank()) "Full name is required." else null

        studentNumError = when {
            studentNumber.isBlank()                       -> "Student number is required."
            !STUDENT_NUMBER_RE.matches(studentNumber.trim()) ->
                "Must be 11 digits starting with 02000 (e.g. 02000123456)."
            else                                          -> null
        }

        emailError = if (email.isBlank() || !email.contains('@'))
            "Enter a valid email address." else null

        passwordError = when {
            password.length < 8 -> "Password must be at least 8 characters."
            else                 -> null
        }
        confirmError = when {
            confirmPassword.isBlank()   -> "Please confirm your password."
            confirmPassword != password -> "Passwords do not match."
            else                        -> null
        }
        return listOf(fullNameError, studentNumError, emailError, passwordError, confirmError)
            .all { it == null }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MedKitBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 40.dp)
    ) {
        // ── Back arrow ────────────────────────────────────────────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint               = MedKitInkMain
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        BrandMark()
        Spacer(Modifier.height(28.dp))

        Text("Create your account", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MedKitInkMain)
        Spacer(Modifier.height(6.dp))
        Text(
            "Set up a MedKit profile so responders and clinic staff know who they're helping.",
            style = MaterialTheme.typography.bodyMedium, color = MedKitInkSoft
        )

        Spacer(Modifier.height(28.dp))

        // ── Full Name ──────────────────────────────────────────────────────────
        MedKitTextField(
            value         = fullName,
            onValueChange = { fullName = it; fullNameError = null; viewModel.clearError() },
            label         = "Full Name",
            leadingIcon   = { Icon(Icons.Filled.Person, null, tint = MedKitInkFaint) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            errorMessage  = fullNameError,
            modifier      = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // ── Student Number ────────────────────────────────────────────────────
        MedKitTextField(
            value         = studentNumber,
            onValueChange = { v ->
                if (v.length <= 11 && v.all { it.isDigit() }) {
                    studentNumber = v
                    studentNumError = null
                    viewModel.clearError()
                }
            },
            label           = "Student Number",
            placeholder     = "02000XXXXXX",
            leadingIcon     = { Icon(Icons.Filled.Badge, null, tint = MedKitInkFaint) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            errorMessage    = studentNumError,
            modifier        = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // ── Email Address ─────────────────────────────────────────────────────
        MedKitTextField(
            value         = email,
            onValueChange = { email = it; emailError = null; viewModel.clearError() },
            label         = "Email Address",
            leadingIcon   = { Icon(Icons.Filled.Email, null, tint = MedKitInkFaint) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            errorMessage  = emailError,
            modifier      = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // ── Password ──────────────────────────────────────────────────────────
        MedKitTextField(
            value         = password,
            onValueChange = { password = it; passwordError = null; viewModel.clearError() },
            label         = "Password",
            leadingIcon   = { Icon(Icons.Filled.Lock, null, tint = MedKitInkFaint) },
            trailingIcon  = {
                val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(icon, null, tint = MedKitInkFaint)
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None
                                   else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            errorMessage  = passwordError,
            modifier      = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // ── Confirm Password ──────────────────────────────────────────────────
        MedKitTextField(
            value         = confirmPassword,
            onValueChange = { confirmPassword = it; confirmError = null },
            label         = "Confirm Password",
            leadingIcon   = { Icon(Icons.Filled.Lock, null, tint = MedKitInkFaint) },
            visualTransformation = if (passwordVisible) VisualTransformation.None
                                   else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            errorMessage  = confirmError,
            modifier      = Modifier.fillMaxWidth()
        )

        // ── Server-level error ────────────────────────────────────────────────
        uiState.errorMessage?.let { msg ->
            Spacer(Modifier.height(8.dp))
            Text(
                text      = msg,
                color     = MedKitEmergencyRed,
                fontSize  = 13.sp,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(24.dp))

        // ── Send Verification Code ────────────────────────────────────────────
        MedKitButton(
            text        = "Send Verification Code",
            loadingText = "Sending code…",
            loading     = uiState.isLoading,
            onClick     = {
                if (validate()) {
                    viewModel.sendOtp(email.trim().lowercase())
                }
            }
        )

        Spacer(Modifier.height(28.dp))
    }
}
