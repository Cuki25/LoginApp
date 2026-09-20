package com.example.loginapp.ui.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.loginapp.data.SignUpDraft

/**
 * Sign-up Step 1a: collect the user's personal email or phone number and
 * "send" a verification code. This is a MOCK -- no real email/SMS is sent.
 */
@Composable
fun SignUpContactScreen(
    onCodeSent: (contact: String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var contact by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    fun sendCode() {
        val trimmed = contact.trim()
        if (trimmed.isBlank()) {
            error = "Enter your email or phone number."
            return
        }
        SignUpDraft.contact = trimmed
        onCodeSent(trimmed)
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
            "Step 1 of 2 - Verify it's you",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = contact,
            onValueChange = {
                contact = it
                error = null
            },
            label = { Text("Enter personal email or number") },
            leadingIcon = { Icon(Icons.Filled.MailOutline, contentDescription = null) },
            singleLine = true,
            isError = error != null,
            supportingText = { error?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(onClick = { sendCode() }, modifier = Modifier.fillMaxWidth()) {
            Text("Send Code")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onBackToLogin) {
            Text("Already have an account? Log in")
        }
    }
}

/**
 * Sign-up Step 1b: mock OTP verification. Any 6-digit code is accepted, and the
 * "sent" code is shown on screen for testing convenience.
 */
@Composable
fun OtpVerificationScreen(
    contact: String,
    onVerified: () -> Unit,
    onResend: () -> Unit
) {
    // Fixed mock code so testers know what to type. Replace with a real
    // verification service later.
    val mockCode = "123456"

    var code by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    fun verify() {
        when {
            code.length != 6 || code.any { !it.isDigit() } ->
                error = "Enter the 6-digit code."
            code != mockCode ->
                error = "Incorrect code. (Mock code is $mockCode.)"
            else -> onVerified()
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
        Text("Enter verification code", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            "We sent a code to $contact",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "For testing, use code: $mockCode",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = code,
            onValueChange = {
                if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                    code = it
                    error = null
                }
            },
            label = { Text("6-digit code") },
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

        TextButton(onClick = onResend) {
            Text("Didn't get a code? Resend")
        }
    }
}
