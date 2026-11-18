package com.example.loginapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.loginapp.data.AppPreferences
import com.example.loginapp.data.SessionManager
import com.example.loginapp.ui.AppNavigation
import com.example.loginapp.ui.Routes
import com.example.loginapp.ui.theme.LoginAppTheme

class MainActivity : ComponentActivity() {

    private lateinit var session:     SessionManager
    private lateinit var appPrefs:    AppPreferences

    private var oauthDeepLinkUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        session  = SessionManager(this)
        appPrefs = AppPreferences(this)

        oauthDeepLinkUri = intent.extractOAuthUri()

        val startDestination = when {
            oauthDeepLinkUri != null -> Routes.LOGIN
            session.isLoggedIn()     -> Routes.dashboardFor(session.getRole())
            else                     -> Routes.LOGIN
        }

        setContent {
            val darkTheme by appPrefs.darkMode.collectAsState()

            LoginAppTheme(darkTheme = darkTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AppNavigation(
                            session          = session,
                            appPrefs         = appPrefs,
                            startDestination = startDestination,
                            oauthDeepLinkUri = oauthDeepLinkUri
                        )
                        oauthDeepLinkUri = null
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        oauthDeepLinkUri = intent.extractOAuthUri()
    }
}

private fun Intent.extractOAuthUri(): Uri? {
    if (action != Intent.ACTION_VIEW) return null
    val uri = data ?: return null
    if (uri.scheme != "medkit" || uri.host != "auth") return null
    return if (!uri.getQueryParameter("token").isNullOrBlank()) uri else null
}
