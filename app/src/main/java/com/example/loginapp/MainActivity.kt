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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.loginapp.data.SessionManager
import com.example.loginapp.ui.AppNavigation
import com.example.loginapp.ui.Routes
import com.example.loginapp.ui.theme.LoginAppTheme

class MainActivity : ComponentActivity() {

    private lateinit var session: SessionManager

    /**
     * Carries the OAuth callback URI into the Compose tree.
     *
     * Set from [onNewIntent] (Custom Tab callback when the activity is already
     * running) or from [onCreate] (cold start triggered by the deep-link).
     * Reset to null after AppNavigation consumes it so it isn't re-processed on
     * recomposition.
     */
    private var oauthDeepLinkUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        session = SessionManager(this)

        // Check if this cold-start was triggered by the OAuth deep-link.
        oauthDeepLinkUri = intent.extractOAuthUri()

        // Determine where the nav graph should start:
        //  • Active session  → dashboard (bypass login)
        //  • OAuth deep-link → AppNavigation will handle the navigation
        //  • Neither         → login screen
        val startDestination = when {
            oauthDeepLinkUri != null -> Routes.LOGIN
            session.isLoggedIn()     -> Routes.dashboardFor(session.getRole())
            else                     -> Routes.LOGIN
        }

        setContent {
            LoginAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                    ) {
                        AppNavigation(
                            session          = session,
                            startDestination = startDestination,
                            oauthDeepLinkUri = oauthDeepLinkUri
                        )
                        // Clear after handing off so recompositions don't re-navigate.
                        oauthDeepLinkUri = null
                    }
                }
            }
        }
    }

    /**
     * Called when the Chrome Custom Tab redirects back to the app via the
     * medkit:// deep-link while the activity is already in the foreground
     * (singleTop or singleTask launch mode).
     *
     * The updated [oauthDeepLinkUri] state triggers recomposition and
     * AppNavigation picks it up.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        oauthDeepLinkUri = intent.extractOAuthUri()
    }
}

// ── Extension: pull the OAuth URI out of an Intent if it's a deep-link ───────

private fun Intent.extractOAuthUri(): Uri? {
    if (action != Intent.ACTION_VIEW) return null
    val uri = data ?: return null
    if (uri.scheme != "medkit" || uri.host != "auth") return null
    // Must carry a token to be a valid callback — ignore plain navigation links.
    return if (!uri.getQueryParameter("token").isNullOrBlank()) uri else null
}
