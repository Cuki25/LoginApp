package com.example.loginapp.ui.signup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import com.example.loginapp.data.Campuses
import com.example.loginapp.data.SignUpDraft
import com.example.loginapp.ui.components.LabeledDropdown
import com.example.loginapp.util.Validation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpBasicInfoScreen(
    onContinue: () -> Unit
) {
    var campusName by remember { mutableStateOf(Campuses.displayNameForOrEmpty(SignUpDraft.campusId)) }
    var studentId by remember { mutableStateOf(SignUpDraft.studentId) }
    var firstName by remember { mutableStateOf(SignUpDraft.firstName) }
    var lastName by remember { mutableStateOf(SignUpDraft.lastName) }
    var middleName by remember { mutableStateOf(SignUpDraft.middleName) }
    var middleNA by remember { mutableStateOf(SignUpDraft.middleNameNA) }
    var birthdayMillis by remember { mutableStateOf(SignUpDraft.birthdayMillis) }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }

    val dateText = birthdayMillis?.let {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(it))
    } ?: ""

    fun validateAndContinue() {
        formError = when {
            campusName.isBlank() -> "Please select your campus."
            Validation.requiredIdError(studentId, "Student ID") != null ->
                Validation.requiredIdError(studentId, "Student ID")
            firstName.isBlank() -> "First name is required."
            lastName.isBlank() -> "Last name is required."
            !middleNA && middleName.isBlank() -> "Enter a middle name or check N/A."
            birthdayMillis == null -> "Please select your birthday."
            Validation.passwordError(password) != null -> Validation.passwordError(password)
            Validation.confirmPasswordError(password, confirm) != null ->
                Validation.confirmPasswordError(password, confirm)
            else -> null
        }
        if (formError != null) return

        SignUpDraft.campusId = Campuses.all.first { it.displayName == campusName }.id
        SignUpDraft.studentId = studentId.trim()
        SignUpDraft.firstName = firstName.trim()
        SignUpDraft.lastName = lastName.trim()
        SignUpDraft.middleName = if (middleNA) "" else middleName.trim()
        SignUpDraft.middleNameNA = middleNA
        SignUpDraft.birthdayMillis = birthdayMillis
        SignUpDraft.password = password
        onContinue()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text("Basic info", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            "Step 2 of 2 - Tell us about you",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(24.dp))

        LabeledDropdown(
            label = "Select Campus",
            options = Campuses.all.map { it.displayName },
            selected = campusName,
            onSelected = { campusName = it; formError = null },
            placeholder = "Select your campus",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = studentId,
            onValueChange = { studentId = it; formError = null },
            label = { Text("Student ID") },
            leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it; formError = null },
            label = { Text("First Name") },
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it; formError = null },
            label = { Text("Last Name") },
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = if (middleNA) "" else middleName,
            onValueChange = { middleName = it; formError = null },
            label = { Text("Middle Name") },
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
            singleLine = true,
            enabled = !middleNA,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = middleNA,
                onCheckedChange = { middleNA = it; formError = null }
            )
            Text("No middle name (N/A)")
        }

        Spacer(Modifier.height(12.dp))

        // Birthday - read-only field that opens a date picker dialog.
        // A disabled TextField can swallow clicks, so we put a transparent
        // clickable overlay on top of an enabled-but-readOnly field instead.
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = dateText,
                onValueChange = {},
                readOnly = true,
                label = { Text("Birthday") },
                leadingIcon = { Icon(Icons.Filled.Cake, contentDescription = null) },
                placeholder = { Text("Select your birth date") },
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker = true }
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; formError = null },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
            trailingIcon = {
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(if (passwordVisible) "Hide" else "Show")
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it; formError = null },
            label = { Text("Retype Password") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
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

        Button(onClick = { validateAndContinue() }, modifier = Modifier.fillMaxWidth()) {
            Text("Continue")
        }

        Spacer(Modifier.height(24.dp))
    }

    if (showDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = birthdayMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    birthdayMillis = state.selectedDateMillis
                    showDatePicker = false
                    formError = null
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = state)
        }
    }
}
