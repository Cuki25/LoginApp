package com.example.loginapp.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.data.AppPreferences
import com.example.loginapp.ui.components.MedKitButton
import com.example.loginapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    email:    String,
    appPrefs: AppPreferences,
    onLogout: () -> Unit
) {
    var fullName   by remember { mutableStateOf("") }
    var bloodType  by remember { mutableStateOf("Unknown") }
    var allergies  by remember { mutableStateOf("") }
    var conditions by remember { mutableStateOf("") }

    val darkMode  by appPrefs.darkMode.collectAsState()
    val readAloud by appPrefs.readAloud.collectAsState()

    val bloodOptions = listOf("Unknown","O+","O-","A+","A-","B+","B-","AB+","AB-")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Column(
            modifier            = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier         = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            ) {
                Text(
                    text     = email.firstOrNull()?.uppercase() ?: "?",
                    fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color    = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(email, fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f))
            Text("STI College Global City", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // ── Account details ───────────────────────────────────────────────
            SectionLabel("Account details")

            OutlinedTextField(
                value         = fullName,
                onValueChange = { fullName = it },
                label         = { Text("Full name") },
                singleLine    = true,
                shape         = FieldShape,
                colors        = profileFieldColors(),
                modifier      = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = email,
                onValueChange = {},
                label         = { Text("Email address") },
                readOnly      = true,
                singleLine    = true,
                supportingText = { Text("Your email is your login and can't be changed here.",
                    fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                shape         = FieldShape,
                colors        = profileFieldColors(),
                modifier      = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            MedKitButton(text = "Save account details", onClick = {})

            Spacer(Modifier.height(24.dp))

            // ── Medical information ───────────────────────────────────────────
            SectionLabel("Medical information")
            Text(
                "Your emergency medical profile is saved to your account.",
                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp
            )
            Spacer(Modifier.height(10.dp))

            var btExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded         = btExpanded,
                onExpandedChange = { btExpanded = it },
                modifier         = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value          = bloodType,
                    onValueChange  = {},
                    readOnly       = true,
                    label          = { Text("Blood type") },
                    trailingIcon   = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = btExpanded) },
                    shape          = FieldShape,
                    colors         = profileFieldColors(),
                    modifier       = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = btExpanded, onDismissRequest = { btExpanded = false }) {
                    bloodOptions.forEach { opt ->
                        DropdownMenuItem(
                            text    = { Text(opt, color = MaterialTheme.colorScheme.onSurface) },
                            onClick = { bloodType = opt; btExpanded = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = allergies,
                onValueChange = { allergies = it },
                label         = { Text("Known allergies") },
                shape         = FieldShape,
                colors        = profileFieldColors(),
                modifier      = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = conditions,
                onValueChange = { conditions = it },
                label         = { Text("Known illnesses / conditions") },
                shape         = FieldShape,
                colors        = profileFieldColors(),
                modifier      = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            MedKitButton(text = "Save medical profile", onClick = {})

            Spacer(Modifier.height(24.dp))

            // ── Preferences ───────────────────────────────────────────────────
            SectionLabel("Preferences")

            PreferenceRow(
                icon     = Icons.Filled.DarkMode,
                title    = "Dark mode",
                subtitle = "Forest green dark theme",
                checked  = darkMode,
                onToggle = { appPrefs.setDarkMode(it) }
            )
            Spacer(Modifier.height(8.dp))
            PreferenceRow(
                icon     = Icons.Filled.RecordVoiceOver,
                title    = "Read steps aloud",
                subtitle = "Auto-advance TTS on first-aid steps",
                checked  = readAloud,
                onToggle = { appPrefs.setReadAloud(it) }
            )

            Spacer(Modifier.height(24.dp))

            // ── Account actions ───────────────────────────────────────────────
            SectionLabel("Account")

            Button(
                onClick  = onLogout,
                shape    = FieldShape,
                colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Log out", fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onError) }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick  = {},
                shape    = FieldShape,
                border   = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Delete account", color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium, fontSize = 15.sp)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.5.sp)
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun PreferenceRow(
    icon:     androidx.compose.ui.graphics.vector.ImageVector,
    title:    String,
    subtitle: String,
    checked:  Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape  = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier          = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
                checked         = checked,
                onCheckedChange = onToggle,
                colors          = SwitchDefaults.colors(
                    checkedThumbColor  = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor  = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun profileFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
    unfocusedBorderColor    = MaterialTheme.colorScheme.outline,
    focusedBorderColor      = MaterialTheme.colorScheme.primary
)
