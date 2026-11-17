package com.example.loginapp.ui.dashboard

import androidx.compose.runtime.Composable
import com.example.loginapp.ui.main.MainAppScaffold

/** Role: "user" — routes into the full MedKit app scaffold. */
@Composable
fun UserDashboard(email: String, onLogout: () -> Unit) {
    MainAppScaffold(email = email, onLogout = onLogout)
}
