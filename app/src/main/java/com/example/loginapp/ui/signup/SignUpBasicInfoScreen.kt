package com.example.loginapp.ui.signup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.loginapp.data.SignUpDraft
import com.example.loginapp.ui.components.LabeledDropdown
import java.text.SimpleDateFormat
import java.util.*

/**
 * Step 2 (post-OTP): collects personal & medical info only.
 * Name, student number, email, and password were all captured in RegisterScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpBasicInfoScreen(
    onContinue: () -> Unit
) {
    var birthdayMillis  by remember { mutableStateOf(SignUpDraft.birthdayMillis) }
    var bloodType       by remember { mutableStateOf(SignUpDraft.bloodType.ifBlank { "Unknown" }) }
    var allergies       by remember { mutableStateOf(SignUpDraft.allergies) }
    var conditions      by remember { mutableStateOf(SignUpDraft.conditions.joinToString(", ")) }

    // Emergency contact
    var ecName          by remember { mutableStateOf("") }
    var ecRelationship  by remember { mutableStateOf("") }
    var ecPhone         by remember { mutableStateOf("") }

    var showDatePicker  by remember { mutableStateOf(false) }
    var formError       by remember { mutableStateOf<String?>(null) }

    val bloodOptions = listOf("Unknown","A+","A-","B+","B-","AB+","AB-","O+","O-")

    val dateText = birthdayMillis?.let {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(it))
    } ?: ""

    fun save() {
        SignUpDraft.birthdayMillis = birthdayMillis
        SignUpDraft.bloodType      = bloodType
        SignUpDraft.allergies      = allergies
        SignUpDraft.conditions     = conditions.split(",").map { it.trim() }.filter { it.isNotBlank() }
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
        Text("Your health profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text("Step 2 — Medical & emergency contact info",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(Modifier.height(24.dp))

        // ── Birthday ──────────────────────────────────────────────────────────
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value         = dateText,
                onValueChange = {},
                readOnly      = true,
                label         = { Text("Birthday") },
                leadingIcon   = { Icon(Icons.Filled.Cake, null) },
                placeholder   = { Text("Select your birth date") },
                modifier      = Modifier.fillMaxWidth()
            )
            Box(modifier = Modifier.matchParentSize().clickable { showDatePicker = true })
        }

        Spacer(Modifier.height(12.dp))

        // ── Blood Type ────────────────────────────────────────────────────────
        LabeledDropdown(
            label     = "Blood Type",
            options   = bloodOptions,
            selected  = bloodType,
            onSelected = { bloodType = it; formError = null },
            modifier  = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // ── Allergies ─────────────────────────────────────────────────────────
        OutlinedTextField(
            value         = allergies,
            onValueChange = { allergies = it },
            label         = { Text("Known allergies") },
            leadingIcon   = { Icon(Icons.Filled.Warning, null) },
            modifier      = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // ── Conditions ────────────────────────────────────────────────────────
        OutlinedTextField(
            value         = conditions,
            onValueChange = { conditions = it },
            label         = { Text("Known illnesses / conditions") },
            leadingIcon   = { Icon(Icons.Filled.MedicalServices, null) },
            modifier      = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Text("Emergency contact", style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value         = ecName,
            onValueChange = { ecName = it },
            label         = { Text("Full name") },
            leadingIcon   = { Icon(Icons.Filled.Person, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier      = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value         = ecRelationship,
            onValueChange = { ecRelationship = it },
            label         = { Text("Relationship (e.g. Mother, Father)") },
            leadingIcon   = { Icon(Icons.Filled.People, null) },
            modifier      = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value           = ecPhone,
            onValueChange   = { ecPhone = it },
            label           = { Text("Phone number") },
            leadingIcon     = { Icon(Icons.Filled.Phone, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier        = Modifier.fillMaxWidth()
        )

        if (formError != null) {
            Spacer(Modifier.height(8.dp))
            Text(formError!!, color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(28.dp))

        Button(onClick = { save() }, modifier = Modifier.fillMaxWidth()) {
            Text("Save & continue")
        }

        Spacer(Modifier.height(10.dp))

        TextButton(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
            Text("Skip for now")
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
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) { DatePicker(state = state) }
    }
}
