package com.example.loginapp.ui

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.loginapp.data.SessionManager
import com.example.loginapp.data.SignUpDraft
import com.example.loginapp.ui.auth.AuthViewModel
import com.example.loginapp.ui.auth.LoginScreen
import com.example.loginapp.ui.auth.OtpVerificationScreen
import com.example.loginapp.ui.auth.RegisterScreen
import com.example.loginapp.ui.dashboard.AdminDashboard
import com.example.loginapp.ui.dashboard.SuperAdminDashboard
import com.example.loginapp.ui.dashboard.UserDashboard
import com.example.loginapp.ui.signup.ContactInfoScreen
import com.example.loginapp.ui.signup.MedicalProfileScreen
import com.example.loginapp.ui.signup.SignUpBasicInfoScreen

// ── Route constants ───────────────────────────────────────────────────────────

object Routes {
    // Auth
    const val LOGIN    = "login"
    const val REGISTER = "register"
    const val OTP_VERIFY = "otp_verify/{email}"    // path param: url-encoded email
    fun otpVerify(email: String) = "otp_verify/${Uri.encode(email)}"

    // Role-based dashboards
    const val USER_DASHBOARD        = "user_dashboard"
    const val ADMIN_DASHBOARD       = "admin_dashboard"
    const val SUPER_ADMIN_DASHBOARD = "super_admin_dashboard"

    // Legacy multi-step signup profile steps (kept, not entry points)
    const val SIGNUP_BASIC    = "signup/basic"
    const val SIGNUP_MEDICAL  = "signup/medical"
    const val SIGNUP_CONTACTS = "signup/contacts"

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Returns the correct dashboard route for [role]. */
    fun dashboardFor(role: String): String = when (role) {
        "admin"       -> ADMIN_DASHBOARD
        "super_admin" -> SUPER_ADMIN_DASHBOARD
        else          -> USER_DASHBOARD
    }
}

// ── Root navigation graph ──────────────────────────────────────────────────────

@Composable
fun AppNavigation(
    session:          SessionManager,
    startDestination: String,
    oauthDeepLinkUri: Uri? = null
) {
    val navController = rememberNavController()
    val context       = LocalContext.current

    // Single shared AuthViewModel across auth screens — survives configuration change.
    val authViewModel: AuthViewModel = viewModel()

    // ── Process OAuth deep-link callback ──────────────────────────────────────
    if (oauthDeepLinkUri != null) {
        val token    = oauthDeepLinkUri.getQueryParameter("token")
        val email    = oauthDeepLinkUri.getQueryParameter("email")
        val userId   = oauthDeepLinkUri.getQueryParameter("userId") ?: ""
        val provider = oauthDeepLinkUri.getQueryParameter("provider") ?: "oauth"
        val role     = oauthDeepLinkUri.getQueryParameter("role")     ?: "user"
        if (!token.isNullOrBlank() && !email.isNullOrBlank()) {
            session.saveSession(token = token, email = email, userId = userId,
                provider = provider, role = role)
            navController.navigate(Routes.dashboardFor(role)) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {

        // ── Login ─────────────────────────────────────────────────────────────
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel          = authViewModel,
                onLoginSuccess     = { data ->
                    session.saveSession(
                        token    = data.token,
                        email    = data.email,
                        userId   = data.userId,
                        role     = data.role
                    )
                    navController.navigate(Routes.dashboardFor(data.role)) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onOAuthGoogle    = { openCustomTab(context, "http://10.0.2.2:8080/auth/google") },
                onOAuthMicrosoft = { openCustomTab(context, "http://10.0.2.2:8080/auth/microsoft") }
            )
        }

        // ── Register ──────────────────────────────────────────────────────────
        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel      = authViewModel,
                onOtpSent      = { email, password ->
                    // Stash password in-memory so OTP screen can call completeRegistration.
                    SignUpDraft.email    = email
                    SignUpDraft.password = password
                    navController.navigate(Routes.otpVerify(email))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── OTP Verification ──────────────────────────────────────────────────
        composable(
            route     = Routes.OTP_VERIFY,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email    = Uri.decode(backStackEntry.arguments?.getString("email").orEmpty())
            val password = SignUpDraft.password   // retrieved from in-memory draft

            OtpVerificationScreen(
                viewModel  = authViewModel,
                email      = email,
                password   = password,
                onVerified = { data ->
                    session.saveSession(
                        token    = data.token,
                        email    = data.email,
                        userId   = data.userId,
                        role     = data.role
                    )
                    SignUpDraft.clear()
                    navController.navigate(Routes.SIGNUP_BASIC) {
                        popUpTo(Routes.LOGIN) { inclusive = false }
                    }
                },
                onResend = { authViewModel.sendOtp(email) }
            )
        }

        // ── Profile steps (post-registration) ────────────────────────────────
        composable(Routes.SIGNUP_BASIC) {
            SignUpBasicInfoScreen(
                onContinue = { navController.navigate(Routes.SIGNUP_MEDICAL) }
            )
        }
        composable(Routes.SIGNUP_MEDICAL) {
            MedicalProfileScreen(
                onContinue = { navController.navigate(Routes.SIGNUP_CONTACTS) },
                onSkip     = { navController.navigate(Routes.SIGNUP_CONTACTS) }
            )
        }
        composable(Routes.SIGNUP_CONTACTS) {
            ContactInfoScreen(
                onFinish = { finishProfileSetup(navController, session) },
                onSkip   = { finishProfileSetup(navController, session) }
            )
        }

        // ── Role-based dashboards ─────────────────────────────────────────────
        composable(Routes.USER_DASHBOARD) {
            UserDashboard(
                email    = session.getEmail().orEmpty(),
                onLogout = { logout(navController, session) }
            )
        }
        composable(Routes.ADMIN_DASHBOARD) {
            AdminDashboard(
                email    = session.getEmail().orEmpty(),
                onLogout = { logout(navController, session) }
            )
        }
        composable(Routes.SUPER_ADMIN_DASHBOARD) {
            SuperAdminDashboard(
                email    = session.getEmail().orEmpty(),
                onLogout = { logout(navController, session) }
            )
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun openCustomTab(context: Context, url: String) {
    CustomTabsIntent.Builder()
        .setShowTitle(true)
        .build()
        .launchUrl(context, Uri.parse(url))
}

private fun finishProfileSetup(navController: NavHostController, session: SessionManager) {
    val role = session.getRole()
    SignUpDraft.clear()
    navController.navigate(Routes.dashboardFor(role)) {
        popUpTo(Routes.LOGIN) { inclusive = true }
    }
}

private fun logout(navController: NavHostController, session: SessionManager) {
    session.clearSession()
    SignUpDraft.clear()
    navController.navigate(Routes.LOGIN) {
        popUpTo(0) { inclusive = true }
    }
}
