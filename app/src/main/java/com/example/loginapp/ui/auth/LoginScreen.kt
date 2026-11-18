package com.example.loginapp.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.loginapp.data.AuthSuccessData
import com.example.loginapp.ui.components.BrandMark
import com.example.loginapp.ui.components.MedKitButton
import com.example.loginapp.ui.components.MedKitOutlineButton
import com.example.loginapp.ui.components.MedKitTextField
import com.example.loginapp.ui.theme.MedKitBackground
import com.example.loginapp.ui.theme.MedKitEmergencyRed
import com.example.loginapp.ui.theme.MedKitInkFaint
import com.example.loginapp.ui.theme.MedKitInkMain
import com.example.loginapp.ui.theme.MedKitInkSoft
import com.example.loginapp.ui.theme.MedKitLine
import com.example.loginapp.ui.theme.MedKitPrimary

@Composable
fun LoginScreen(
    viewModel:         AuthViewModel,
    /** Called with [AuthSuccessData] on success. NavGraph reads `role` and routes. */
    onLoginSuccess:    (AuthSuccessData) -> Unit,
    onNavigateToRegister: () -> Unit,
    onOAuthGoogle:     () -> Unit,
    onOAuthMicrosoft:  () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError      by remember { mutableStateOf<String?>(null) }
    var passwordError   by remember { mutableStateOf<String?>(null) }

    // React to successful login from the ViewModel
    LaunchedEffect(uiState.authSuccess) {
        uiState.authSuccess?.let { data ->
            viewModel.clearAuthSuccess()
            onLoginSuccess(data)
        }
    }

    fun validate(): Boolean {
        emailError    = if (email.isBlank() || !email.contains('@')) "Enter a valid email address." else null
        passwordError = if (password.length < 8) "Password must be at least 8 characters." else null
        return emailError == null && passwordError == null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MedKitBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── Brand mark ────────────────────────────────────────────────────────
        BrandMark(modifier = Modifier.align(Alignment.Start))

        Spacer(Modifier.height(36.dp))

        // ── Heading ───────────────────────────────────────────────────────────
        Text(
            text       = "Welcome back",
            fontSize   = 26.sp,
            fontWeight = FontWeight.Bold,
            color      = MedKitInkMain,
            modifier   = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text     = "Sign in with your email to reach first aid guidance and campus health resources.",
            style    = MaterialTheme.typography.bodyMedium,
            color    = MedKitInkSoft,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(Modifier.height(28.dp))

        // ── School Email ──────────────────────────────────────────────────────
        MedKitTextField(
            value         = email,
            onValueChange = { email = it; emailError = null; viewModel.clearError() },
            label         = "Email Address",
            leadingIcon   = { Icon(Icons.Filled.Email, contentDescription = null, tint = MedKitInkFaint) },
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
            leadingIcon   = { Icon(Icons.Filled.Lock, contentDescription = null, tint = MedKitInkFaint) },
            trailingIcon  = {
                val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(icon, contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = MedKitInkFaint)
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None
                                   else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            errorMessage  = passwordError,
            modifier      = Modifier.fillMaxWidth()
        )

        // ── Forgot password ───────────────────────────────────────────────────
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { /* TODO: forgot password flow */ }) {
                Text("Forgot password?", color = MedKitPrimary,
                    fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }

        // ── Server-level error banner ─────────────────────────────────────────
        uiState.errorMessage?.let { msg ->
            Spacer(Modifier.height(4.dp))
            Text(
                text      = msg,
                color     = MedKitEmergencyRed,
                fontSize  = 13.sp,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(20.dp))

        // ── Log In button ─────────────────────────────────────────────────────
        MedKitButton(
            text        = "Log in",
            loadingText = "Signing in…",
            loading     = uiState.isLoading,
            onClick     = {
                if (validate()) {
                    viewModel.login(email, password)
                }
            }
        )

        Spacer(Modifier.height(14.dp))

        // ── Demo account shortcut ─────────────────────────────────────────────
        MedKitOutlineButton(
            text    = "Use demo student account",
            onClick = {
                email    = "demo@taguig.edu.ph"
                password = "demo1234"
                emailError    = null
                passwordError = null
                viewModel.clearError()
            }
        )

        Spacer(Modifier.height(24.dp))

        // ── OAuth divider ─────────────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(Modifier.weight(1f), color = MedKitLine)
            Text(
                "  or continue with  ",
                style = MaterialTheme.typography.bodySmall,
                color = MedKitInkFaint
            )
            HorizontalDivider(Modifier.weight(1f), color = MedKitLine)
        }

        Spacer(Modifier.height(14.dp))

        MedKitOutlineButton(
            text    = "Continue with Google",
            onClick = onOAuthGoogle,
            leadingContent = {
                Text("G", fontWeight = FontWeight.Bold,
                    fontSize = 16.sp, color = MedKitPrimary)
            }
        )

        Spacer(Modifier.height(10.dp))

        MedKitOutlineButton(
            text    = "Continue with Microsoft",
            onClick = onOAuthMicrosoft,
            leadingContent = {
                Text("M", fontWeight = FontWeight.Bold,
                    fontSize = 16.sp, color = MedKitPrimary)
            }
        )

        Spacer(Modifier.height(28.dp))

        // ── Register link ─────────────────────────────────────────────────────
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier              = Modifier.fillMaxWidth()
        ) {
            Text("Don't have an account? ", style = MaterialTheme.typography.bodyMedium,
                color = MedKitInkSoft)
            TextButton(
                onClick        = onNavigateToRegister,
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
            ) {
                Text("Register now", color = MedKitPrimary,
                    fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
