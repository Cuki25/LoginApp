package com.example.loginapp.ui

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.loginapp.data.AuthRepository
import com.example.loginapp.data.AuthResult
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (token: String, email: String, userId: String) -> Unit,
    onNavigateToSignUp: () -> Unit,
    onOAuthGoogle: () -> Unit,
    onOAuthMicrosoft: () -> Unit
) {
    val repo  = remember { AuthRepository() }
    val scope = rememberCoroutineScope()

    var email          by remember { mutableStateOf("") }
    var password       by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError    by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var formError     by remember { mutableStateOf<String?>(null) }
    var isLoading     by remember { mutableStateOf(false) }

    fun validate(): Boolean {
        emailError = if (email.isBlank() || !email.contains('@'))
            "Enter a valid email address." else null
        passwordError = if (password.length < 8)
            "Password must be at least 8 characters." else null
        return emailError == null && passwordError == null
    }

    fun submit() {
        formError = null
        if (!validate()) return
        isLoading = true
        scope.launch {
            when (val result = repo.login(email, password)) {
                is AuthResult.Success -> onLoginSuccess(result.token, result.email, result.userId)
                is AuthResult.Error   -> { formError = result.message; isLoading = false }
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
        Text("Welcome back", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            "Log in to your MedKit account",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(32.dp))

        // ── Email ─────────────────────────────────────────────────────────────
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

        // ── Password ──────────────────────────────────────────────────────────
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; passwordError = null; formError = null },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(icon, contentDescription = if (passwordVisible) "Hide" else "Show")
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None
                                   else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            isError = passwordError != null,
            supportingText = { passwordError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        // ── Form-level error (from server) ────────────────────────────────────
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

        // ── Log In button ─────────────────────────────────────────────────────
        Button(
            onClick = { if (!isLoading) submit() },
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
                Text("Signing in…")
            } else {
                Text("Log In")
            }
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onNavigateToSignUp, modifier = Modifier.fillMaxWidth()) {
            Text("Don't have an account? Sign up")
        }

        Spacer(Modifier.height(24.dp))

        // ── OAuth divider ─────────────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Divider(Modifier.weight(1f))
            Text(
                "  or continue with  ",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Divider(Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))

        // ── Google OAuth button ───────────────────────────────────────────────
        OutlinedButton(
            onClick = onOAuthGoogle,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Using a text G since we can't bundle an image asset here.
            // Replace with an Image(painterResource(R.drawable.ic_google))
            // once you add the Google logo to your drawable resources.
            Text(
                "G",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Text("Continue with Google")
        }

        Spacer(Modifier.height(12.dp))

        // ── Microsoft OAuth button ────────────────────────────────────────────
        OutlinedButton(
            onClick = onOAuthMicrosoft,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "M",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Text("Continue with Microsoft")
        }

        Spacer(Modifier.height(24.dp))
    }
}
