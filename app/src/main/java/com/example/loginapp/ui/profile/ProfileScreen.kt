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
import com.example.loginapp.ui.components.MedKitButton
import com.example.loginapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    email:    String,
    onLogout: () -> Unit
) {
    var fullName  by remember { mutableStateOf("") }
    var bloodType by remember { mutableStateOf("Unknown") }
    var allergies by remember { mutableStateOf("") }
    var conditions by remember { mutableStateOf("") }
    var darkMode  by remember { mutableStateOf(false) }
    var readAloud by remember { mutableStateOf(false) }

    val bloodOptions = listOf("Unknown","O+","O-","A+","A-","B+","B-","AB+","AB-")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MedKitBackground)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Column(
            modifier            = Modifier
                .background(MedKitPrimary)
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
                    fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(email, fontSize = 13.sp, color = Color.White.copy(alpha = 0.9f))
            Text("STI College Global City", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
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
                label         = { Text("School email") },
                readOnly      = true,
                singleLine    = true,
                supportingText = { Text("Your email is your login and can't be changed here.",
                    fontSize = 11.sp, color = MedKitInkFaint) },
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
                "Shown to clinic staff and responders if they open your profile during an emergency.",
                fontSize = 12.sp, color = MedKitInkSoft, lineHeight = 18.sp
            )
            Spacer(Modifier.height(10.dp))

            // Blood type dropdown
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
                            text    = { Text(opt, color = MedKitInkMain) },
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
                icon    = Icons.Filled.DarkMode,
                title   = "Dark mode",
                subtitle = "Easier on the eyes at night",
                checked = darkMode,
                onToggle = { darkMode = it }
            )
            Spacer(Modifier.height(8.dp))
            PreferenceRow(
                icon    = Icons.Filled.RecordVoiceOver,
                title   = "Read steps aloud",
                subtitle = "Text-to-speech on every first-aid step",
                checked = readAloud,
                onToggle = { readAloud = it }
            )

            Spacer(Modifier.height(24.dp))

            // ── Account actions ───────────────────────────────────────────────
            SectionLabel("Account")

            Button(
                onClick  = onLogout,
                shape    = FieldShape,
                colors   = ButtonDefaults.buttonColors(containerColor = MedKitEmergencyRed),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Log out", fontWeight = FontWeight.SemiBold, fontSize = 15.sp) }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick  = {},
                shape    = FieldShape,
                border   = androidx.compose.foundation.BorderStroke(1.dp, MedKitEmergencyRed),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Delete account", color = MedKitEmergencyRed,
                    fontWeight = FontWeight.Medium, fontSize = 15.sp)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
        color = MedKitInkFaint, letterSpacing = 0.5.sp)
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
        colors = CardDefaults.cardColors(containerColor = MedKitSurface),
        shape  = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier          = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = MedKitPrimary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MedKitInkMain)
                Text(subtitle, fontSize = 12.sp, color = MedKitInkFaint)
            }
            Switch(
                checked         = checked,
                onCheckedChange = onToggle,
                colors          = SwitchDefaults.colors(
                    checkedThumbColor  = Color.White,
                    checkedTrackColor  = MedKitPrimary
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun profileFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = MedKitSurfaceSecondary,
    focusedContainerColor   = MedKitSurfaceSecondary,
    unfocusedBorderColor    = MedKitLine,
    focusedBorderColor      = MedKitPrimary
)
