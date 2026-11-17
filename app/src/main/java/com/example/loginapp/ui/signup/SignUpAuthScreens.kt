package com.example.loginapp.ui.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.loginapp.data.AuthRepository
import com.example.loginapp.data.AuthResult
import com.example.loginapp.data.AuthSuccessData
import com.example.loginapp.data.SignUpDraft
import kotlinx.coroutines.launch

/**
 * Sign-up Step 1: collect email address, validate it, hit /auth/register, then
 * proceed to the mock OTP screen.
 *
 * Also exposes Google and Microsoft OAuth shortcuts that call [onOAuthGoogle] /
 * [onOAuthMicrosoft] — these open a Chrome Custom Tab from [AppNavigation].
 *
 * [onRegistered] is called with (token, email, userId) on a successful
 * /auth/register response so [AppNavigation] can save the session immediately.
 */
@Composable
fun SignUpContactScreen(
    onRegistered: (token: String, email: String, userId: String) -> Unit,
    onOAuthGoogle: () -> Unit,
    onOAuthMicrosoft: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val repo  = remember { AuthRepository() }
    val scope = rememberCoroutineScope()

    var email      by remember { mutableStateOf("") }
    var password   by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var pwError    by remember { mutableStateOf<String?>(null) }
    var formError  by remember { mutableStateOf<String?>(null) }
    var isLoading  by remember { mutableStateOf(false) }

    fun validate(): Boolean {
        emailError = if (email.isBlank() || !email.contains('@'))
            "Enter a valid email address." else null
        pwError = if (password.length < 8)
            "Password must be at least 8 characters." else null
        return emailError == null && pwError == null
    }

    fun register() {
        formError = null
        if (!validate()) return
        isLoading = true
        scope.launch {
            when (val result = repo.register(email.trim(), password)) {
                is AuthResult.Success -> {
                    val data = result.data
                    SignUpDraft.email            = data.email
                    SignUpDraft.password         = password
                    SignUpDraft.registeredToken  = data.token
                    SignUpDraft.registeredUserId = data.userId
                    onRegistered(data.token, data.email, data.userId)
                }
                is AuthResult.Error -> {
                    formError = result.message
                    isLoading = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create your account", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            "Step 1 — Create your login",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(32.dp))

        // ── Email ──────────────────────────────────────────────────────────
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; emailError = null; formError = null },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            isError = emailError != null,
            supportingText = { emailError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // ── Password ───────────────────────────────────────────────────────
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; pwError = null; formError = null },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            isError = pwError != null,
            supportingText = {
                pwError?.let { Text(it) }
                    ?: Text("At least 8 characters", color = MaterialTheme.colorScheme.onSurfaceVariant)
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (formError != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = formError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { if (!isLoading) register() },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(8.dp))
                Text("Creating account…")
            } else {
                Text("Create Account")
            }
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onBackToLogin, modifier = Modifier.fillMaxWidth()) {
            Text("Already have an account? Log in")
        }

        Spacer(Modifier.height(24.dp))

        // ── OAuth shortcuts ────────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Divider(Modifier.weight(1f))
            Text(
                "  or sign up with  ",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Divider(Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onOAuthGoogle,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("G", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text("Continue with Google")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onOAuthMicrosoft,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("M", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text("Continue with Microsoft")
        }

        Spacer(Modifier.height(24.dp))
    }
}

/**
 * Mock OTP screen shown after /auth/register succeeds.
 *
 * In a real implementation you would send a code to [email] here and verify it
 * against the server. For now any 6-digit input is accepted.
 */
@Composable
fun OtpVerificationScreen(
    email: String,
    onVerified: () -> Unit,
    onResend: () -> Unit
) {
    // Fixed mock code shown on screen so you can test the flow.
    val mockCode = "123456"

    var code  by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    fun verify() {
        error = when {
            code.length != 6 || code.any { !it.isDigit() } -> "Enter the 6-digit code."
            code != mockCode -> "Incorrect code. (Mock code: $mockCode)"
            else -> null
        }
        if (error == null) onVerified()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Check your email", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            "We sent a 6-digit code to $email",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "For testing, use code: $mockCode",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = code,
            onValueChange = {
                if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                    code = it
                    error = null
                }
            },
            label = { Text("Verification code") },
            leadingIcon = { Icon(Icons.Filled.Pin, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = error != null,
            supportingText = { error?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(onClick = { verify() }, modifier = Modifier.fillMaxWidth()) {
            Text("Verify")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onResend, modifier = Modifier.fillMaxWidth()) {
            Text("Didn't get a code? Resend")
        }
    }
}
