package com.example.loginapp.ui

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.loginapp.data.SessionManager
import com.example.loginapp.data.SignUpDraft
import com.example.loginapp.ui.signup.ContactInfoScreen
import com.example.loginapp.ui.signup.MedicalProfileScreen
import com.example.loginapp.ui.signup.OtpVerificationScreen
import com.example.loginapp.ui.signup.SignUpBasicInfoScreen
import com.example.loginapp.ui.signup.SignUpContactScreen

/** All route strings in one place. */
object Routes {
    const val LOGIN         = "login"
    const val SIGNUP_AUTH   = "signup/auth"
    const val SIGNUP_OTP    = "signup/otp"
    const val SIGNUP_BASIC  = "signup/basic"
    const val SIGNUP_MEDICAL  = "signup/medical"
    const val SIGNUP_CONTACTS = "signup/contacts"

    // email is URL-encoded by the caller before being embedded in the path.
    const val DASHBOARD = "dashboard/{email}"
    fun dashboard(email: String): String {
        // Encode the email so the '@' and '.' don't confuse the nav graph.
        val encoded = Uri.encode(email)
        return "dashboard/$encoded"
    }
}

/**
 * Root navigation graph.
 *
 * @param session           SharedPreferences session — persists JWT across launches.
 * @param startDestination  Determined in [MainActivity] from [SessionManager.isLoggedIn].
 * @param oauthDeepLinkUri  Non-null when MainActivity received a deep-link intent
 *                          (medkit://auth/callback?token=...&email=...) from an OAuth
 *                          callback. AppNavigation processes it once on composition.
 */
@Composable
fun AppNavigation(
    session: SessionManager,
    startDestination: String,
    oauthDeepLinkUri: Uri? = null
) {
    val navController = rememberNavController()
    val context       = LocalContext.current

    // ── Process an incoming OAuth deep-link exactly once ─────────────────────
    // MainActivity passes the URI when the activity was launched/resumed via the
    // medkit://auth/callback deep-link after a Google/Microsoft sign-in.
    if (oauthDeepLinkUri != null) {
        val token  = oauthDeepLinkUri.getQueryParameter("token")
        val email  = oauthDeepLinkUri.getQueryParameter("email")
        val userId = oauthDeepLinkUri.getQueryParameter("userId") ?: ""
        if (!token.isNullOrBlank() && !email.isNullOrBlank()) {
            session.saveSession(
                token    = token,
                email    = email,
                userId   = userId,
                provider = oauthDeepLinkUri.getQueryParameter("provider") ?: "oauth"
            )
            // Navigate to dashboard, clearing the entire back stack.
            navController.navigate(Routes.dashboard(email)) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {

        // ── Login ─────────────────────────────────────────────────────────────
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { token, email, userId ->
                    session.saveSession(token = token, email = email, userId = userId)
                    navController.navigate(Routes.dashboard(email)) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    SignUpDraft.clear()
                    navController.navigate(Routes.SIGNUP_AUTH)
                },
                onOAuthGoogle = {
                    openCustomTab(context, "http://10.0.2.2:8080/auth/google")
                },
                onOAuthMicrosoft = {
                    openCustomTab(context, "http://10.0.2.2:8080/auth/microsoft")
                }
            )
        }

        // ── Sign-up step 1a: email + password (calls /auth/register) ──────────
        composable(Routes.SIGNUP_AUTH) {
            SignUpContactScreen(
                onRegistered = { token, email, userId ->
                    // Account created — save session so the profile steps run
                    // "logged in". The OTP step is still shown for UX continuity.
                    session.saveSession(token = token, email = email, userId = userId)
                    navController.navigate(Routes.SIGNUP_OTP)
                },
                onOAuthGoogle    = { openCustomTab(context, "http://10.0.2.2:8080/auth/google") },
                onOAuthMicrosoft = { openCustomTab(context, "http://10.0.2.2:8080/auth/microsoft") },
                onBackToLogin    = { navController.popBackStack(Routes.LOGIN, inclusive = false) }
            )
        }

        // ── Sign-up step 1b: mock OTP verification ────────────────────────────
        composable(Routes.SIGNUP_OTP) {
            OtpVerificationScreen(
                email      = SignUpDraft.email,
                onVerified = { navController.navigate(Routes.SIGNUP_BASIC) },
                onResend   = { /* mock — nothing to resend */ }
            )
        }

        // ── Sign-up step 2: basic profile info ────────────────────────────────
        composable(Routes.SIGNUP_BASIC) {
            SignUpBasicInfoScreen(
                onContinue = { navController.navigate(Routes.SIGNUP_MEDICAL) }
            )
        }

        // ── Sign-up step 3: medical profile (skippable) ───────────────────────
        composable(Routes.SIGNUP_MEDICAL) {
            MedicalProfileScreen(
                onContinue = { navController.navigate(Routes.SIGNUP_CONTACTS) },
                onSkip     = { navController.navigate(Routes.SIGNUP_CONTACTS) }
            )
        }

        // ── Sign-up step 4: contact information (skippable) ───────────────────
        composable(Routes.SIGNUP_CONTACTS) {
            ContactInfoScreen(
                onFinish = { finishSignUp(navController, session) },
                onSkip   = { finishSignUp(navController, session) }
            )
        }

        // ── Dashboard ─────────────────────────────────────────────────────────
        composable(
            route     = Routes.DASHBOARD,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = Uri.decode(
                backStackEntry.arguments?.getString("email").orEmpty()
            )
            HomeScreen(
                // HomeScreen still uses studentId/schoolId params for display;
                // pass email as studentId, leave schoolId blank until the full
                // profile migration in Phase 2.
                studentId = email,
                schoolId  = session.getUserId().orEmpty(),
                onLogout  = {
                    session.clearSession()
                    SignUpDraft.clear()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

/**
 * Opens [url] in a Chrome Custom Tab. The OAuth flow redirects back via the
 * medkit:// deep-link, which Android routes back to MainActivity.
 */
private fun openCustomTab(context: Context, url: String) {
    CustomTabsIntent.Builder()
        .setShowTitle(true)
        .build()
        .launchUrl(context, Uri.parse(url))
}

/**
 * Completes the signup flow: captures the email from the draft before clearing
 * it, then navigates to the dashboard with the full back stack removed.
 */
private fun finishSignUp(navController: NavHostController, session: SessionManager) {
    val email = SignUpDraft.email.ifBlank { session.getEmail().orEmpty() }
    SignUpDraft.clear()
    navController.navigate(Routes.dashboard(email)) {
        popUpTo(Routes.LOGIN) { inclusive = true }
    }
}
