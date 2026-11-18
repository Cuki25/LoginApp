package com.example.loginapp.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.components.BrandMark
import com.example.loginapp.ui.components.MedKitButton
import com.example.loginapp.ui.components.MedKitTextField
import com.example.loginapp.ui.theme.*

private val STUDENT_NUMBER_RE = Regex("^02000\\d{6}$")

@Composable
fun RegisterScreen(
    viewModel:      AuthViewModel,
    onOtpSent:      (email: String, password: String, studentNumber: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var firstName       by remember { mutableStateOf("") }
    var middleName      by remember { mutableStateOf("") }
    var middleNA        by remember { mutableStateOf(false) }
    var lastName        by remember { mutableStateOf("") }
    var studentNumber   by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var firstNameError    by remember { mutableStateOf<String?>(null) }
    var lastNameError     by remember { mutableStateOf<String?>(null) }
    var middleNameError   by remember { mutableStateOf<String?>(null) }
    var studentNumError   by remember { mutableStateOf<String?>(null) }
    var emailError        by remember { mutableStateOf<String?>(null) }
    var passwordError     by remember { mutableStateOf<String?>(null) }
    var confirmError      by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.otpSent) {
        if (uiState.otpSent) {
            viewModel.clearOtpSent()
            val fullName = buildString {
                append(firstName.trim())
                if (!middleNA && middleName.isNotBlank()) append(" ${middleName.trim()}")
                append(" ${lastName.trim()}")
            }
            onOtpSent(email.trim().lowercase(), password, studentNumber.trim())
        }
    }

    fun validate(): Boolean {
        firstNameError  = if (firstName.isBlank()) "First name is required." else null
        lastNameError   = if (lastName.isBlank()) "Last name is required." else null
        middleNameError = if (!middleNA && middleName.isBlank())
            "Enter a middle name or check N/A." else null

        studentNumError = when {
            studentNumber.isBlank() -> "Student number is required."
            !STUDENT_NUMBER_RE.matches(studentNumber.trim()) ->
                "Must be 11 digits starting with 02000 (e.g. 02000123456)."
            else -> null
        }
        emailError    = if (email.isBlank() || !email.contains('@'))
            "Enter a valid email address." else null
        passwordError = if (password.length < 8)
            "Password must be at least 8 characters." else null
        confirmError  = when {
            confirmPassword.isBlank()   -> "Please confirm your password."
            confirmPassword != password -> "Passwords do not match."
            else                        -> null
        }
        return listOf(firstNameError, lastNameError, middleNameError,
            studentNumError, emailError, passwordError, confirmError).all { it == null }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 40.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack, modifier = Modifier.size(40.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back",
                    tint = MaterialTheme.colorScheme.onBackground)
            }
        }

        Spacer(Modifier.height(8.dp))
        BrandMark()
        Spacer(Modifier.height(28.dp))

        Text("Create your account", fontSize = 26.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(6.dp))
        Text("Set up a MedKit profile so responders and clinic staff know who they're helping.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(Modifier.height(28.dp))

        // ── First Name ────────────────────────────────────────────────────────
        MedKitTextField(
            value           = firstName,
            onValueChange   = { firstName = it; firstNameError = null; viewModel.clearError() },
            label           = "First Name",
            leadingIcon     = { Icon(Icons.Filled.Person, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            errorMessage    = firstNameError,
            modifier        = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        // ── Middle Name ───────────────────────────────────────────────────────
        MedKitTextField(
            value           = if (middleNA) "" else middleName,
            onValueChange   = { middleName = it; middleNameError = null; viewModel.clearError() },
            label           = "Middle Name",
            leadingIcon     = { Icon(Icons.Filled.Person, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            enabled         = !middleNA,
            errorMessage    = middleNameError,
            modifier        = Modifier.fillMaxWidth()
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = middleNA, onCheckedChange = { middleNA = it; middleNameError = null })
            Text("No middle name (N/A)", fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(Modifier.height(4.dp))

        // ── Last Name ─────────────────────────────────────────────────────────
        MedKitTextField(
            value           = lastName,
            onValueChange   = { lastName = it; lastNameError = null; viewModel.clearError() },
            label           = "Last Name",
            leadingIcon     = { Icon(Icons.Filled.Person, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            errorMessage    = lastNameError,
            modifier        = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        // ── Student Number ────────────────────────────────────────────────────
        MedKitTextField(
            value         = studentNumber,
            onValueChange = { v ->
                if (v.length <= 11 && v.all { it.isDigit() }) {
                    studentNumber = v; studentNumError = null; viewModel.clearError()
                }
            },
            label           = "Student Number",
            placeholder     = "02000XXXXXX",
            leadingIcon     = { Icon(Icons.Filled.Badge, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            errorMessage    = studentNumError,
            modifier        = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        // ── Email ─────────────────────────────────────────────────────────────
        MedKitTextField(
            value           = email,
            onValueChange   = { email = it; emailError = null; viewModel.clearError() },
            label           = "Email Address",
            leadingIcon     = { Icon(Icons.Filled.Email, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            errorMessage    = emailError,
            modifier        = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        // ── Password ──────────────────────────────────────────────────────────
        MedKitTextField(
            value           = password,
            onValueChange   = { password = it; passwordError = null; viewModel.clearError() },
            label           = "Password",
            leadingIcon     = { Icon(Icons.Filled.Lock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            trailingIcon    = {
                val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None
                                   else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            errorMessage    = passwordError,
            modifier        = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        // ── Confirm Password ──────────────────────────────────────────────────
        MedKitTextField(
            value           = confirmPassword,
            onValueChange   = { confirmPassword = it; confirmError = null },
            label           = "Retype Password",
            leadingIcon     = { Icon(Icons.Filled.Lock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            visualTransformation = if (passwordVisible) VisualTransformation.None
                                   else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            errorMessage    = confirmError,
            modifier        = Modifier.fillMaxWidth()
        )

        uiState.errorMessage?.let { msg ->
            Spacer(Modifier.height(8.dp))
            Text(msg, color = MaterialTheme.colorScheme.error, fontSize = 13.sp,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }

        Spacer(Modifier.height(24.dp))

        MedKitButton(
            text        = "Send Verification Code",
            loadingText = "Sending code…",
            loading     = uiState.isLoading,
            onClick     = { if (validate()) viewModel.sendOtp(email.trim().lowercase()) }
        )

        Spacer(Modifier.height(28.dp))
    }
}
