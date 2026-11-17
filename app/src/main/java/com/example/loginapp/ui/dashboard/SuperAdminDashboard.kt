package com.example.loginapp.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.components.MedKitButton
import com.example.loginapp.ui.theme.MedKitBackground
import com.example.loginapp.ui.theme.MedKitEmergencyRed
import com.example.loginapp.ui.theme.MedKitInkMain
import com.example.loginapp.ui.theme.MedKitInkSoft

/** Role: "super_admin" — system-wide management. Placeholder for Phase 2. */
@Composable
fun SuperAdminDashboard(email: String, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MedKitBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.ManageAccounts, contentDescription = null,
            tint = MedKitEmergencyRed, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(16.dp))
        Text("Super Admin Dashboard", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MedKitInkMain)
        Spacer(Modifier.height(6.dp))
        Text(email, fontSize = 13.sp, color = MedKitInkSoft, textAlign = TextAlign.Center)
        Spacer(Modifier.height(4.dp))
        Text("Role: Super Admin", fontSize = 12.sp, color = MedKitEmergencyRed)
        Spacer(Modifier.height(32.dp))
        Text(
            "Full super-admin panel coming in Phase 2.\nSystem-wide user management, audit logs, and campus configuration will live here.",
            fontSize = 13.sp, color = MedKitInkSoft, textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        MedKitButton(text = "Log out", onClick = onLogout)
    }
}
