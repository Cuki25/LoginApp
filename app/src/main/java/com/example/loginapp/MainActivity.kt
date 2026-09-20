package com.example.loginapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.loginapp.data.SessionManager
import com.example.loginapp.ui.AppNavigation
import com.example.loginapp.ui.Routes
import com.example.loginapp.ui.theme.LoginAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Read the persisted session on launch and pick where the graph starts.
        val session = SessionManager(this)
        val startDestination = if (session.isLoggedIn()) {
            // Bypass the login screen and go straight to the dashboard,
            // rehydrating the composite key from SharedPreferences.
            Routes.dashboard(
                studentId = session.getStudentId().orEmpty(),
                schoolId = session.getSchoolId().orEmpty()
            )
        } else {
            Routes.LOGIN
        }

        setContent {
            LoginAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        AppNavigation(
                            session = session,
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }
}
