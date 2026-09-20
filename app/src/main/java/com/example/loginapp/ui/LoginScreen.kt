package com.example.loginapp.ui

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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.loginapp.data.AuthResult
import com.example.loginapp.data.Campuses
import com.example.loginapp.data.UserRepository
import com.example.loginapp.ui.components.LabeledDropdown
import com.example.loginapp.util.Validation

@Composable
fun LoginScreen(
    onLoginSuccess: (studentId: String, schoolId: String) -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    var campusName by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var campusError by remember { mutableStateOf<String?>(null) }
    var studentIdError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var formError by remember { mutableStateOf<String?>(null) }

    fun submit() {
        formError = null
        campusError = if (campusName.isBlank()) "Please select your campus." else null
        studentIdError = Validation.requiredIdError(studentId, "Student ID")
        passwordError = Validation.passwordError(password)
        if (campusError != null || studentIdError != null || passwordError != null) return

        // Map the chosen display name back to the backend school_id.
        val schoolId = Campuses.all.first { it.displayName == campusName }.id

        when (val result = UserRepository.login(studentId, schoolId, password)) {
            is AuthResult.Success -> onLoginSuccess(studentId.trim(), schoolId)
            is AuthResult.Error -> formError = result.message
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
        Text(
            text = "Welcome back",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Log in to your MedKit account",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(32.dp))

        LabeledDropdown(
            label = "Select Campus",
            options = Campuses.all.map { it.displayName },
            selected = campusName,
            onSelected = {
                campusName = it
                campusError = null
                formError = null
            },
            placeholder = "Select your campus",
            isError = campusError != null,
            supportingText = campusError,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = studentId,
            onValueChange = {
                studentId = it
                studentIdError = null
                formError = null
            },
            label = { Text("Student ID") },
            leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null) },
            singleLine = true,
            isError = studentIdError != null,
            supportingText = { studentIdError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
                formError = null
            },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(icon, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            isError = passwordError != null,
            supportingText = { passwordError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        if (formError != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = formError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { submit() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log In")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onNavigateToSignUp) {
            Text("Don't have an account? Sign up")
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Demo -> Campus: Taguig City University, Student ID: demo, Password: password123",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
