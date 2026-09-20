package com.example.loginapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.loginapp.data.SessionManager
import com.example.loginapp.data.SignUpDraft
import com.example.loginapp.data.UserRepository
import com.example.loginapp.ui.signup.ContactInfoScreen
import com.example.loginapp.ui.signup.MedicalProfileScreen
import com.example.loginapp.ui.signup.OtpVerificationScreen
import com.example.loginapp.ui.signup.SignUpBasicInfoScreen
import com.example.loginapp.ui.signup.SignUpContactScreen

/** Route names used by the navigation graph. */
object Routes {
    const val LOGIN = "login"

    // Multi-step sign-up flow
    const val SIGNUP_CONTACT = "signup/contact"
    const val SIGNUP_OTP = "signup/otp"
    const val SIGNUP_BASIC = "signup/basic"
    const val SIGNUP_MEDICAL = "signup/medical"
    const val SIGNUP_CONTACTS = "signup/contacts"

    const val DASHBOARD = "dashboard/{studentId}/{schoolId}"
    fun dashboard(studentId: String, schoolId: String) = "dashboard/$studentId/$schoolId"
}

/**
 * @param session          persists the login so the user stays logged in across launches.
 * @param startDestination chosen by MainActivity based on session.isLoggedIn().
 */
@Composable
fun AppNavigation(
    session: SessionManager,
    startDestination: String
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        // ---- Login ----
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { studentId, schoolId ->
                    session.saveLoginSession(studentId, schoolId)
                    navController.navigate(Routes.dashboard(studentId, schoolId)) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    SignUpDraft.clear()
                    navController.navigate(Routes.SIGNUP_CONTACT)
                }
            )
        }

        // ---- Sign-up Step 1a: contact ----
        composable(Routes.SIGNUP_CONTACT) {
            SignUpContactScreen(
                onCodeSent = { navController.navigate(Routes.SIGNUP_OTP) },
                onBackToLogin = { navController.popBackStack(Routes.LOGIN, inclusive = false) }
            )
        }

        // ---- Sign-up Step 1b: OTP ----
        composable(Routes.SIGNUP_OTP) {
            OtpVerificationScreen(
                contact = SignUpDraft.contact,
                onVerified = { navController.navigate(Routes.SIGNUP_BASIC) },
                onResend = { /* mock: nothing to resend */ }
            )
        }

        // ---- Sign-up Step 2: basic info ----
        composable(Routes.SIGNUP_BASIC) {
            SignUpBasicInfoScreen(
                onContinue = {
                    // Account details are now complete -> create the account and
                    // persist the session so the optional screens are "logged in".
                    registerAndLogin(session)
                    navController.navigate(Routes.SIGNUP_MEDICAL)
                }
            )
        }

        // ---- Sign-up Step 3: medical profile (skippable) ----
        composable(Routes.SIGNUP_MEDICAL) {
            MedicalProfileScreen(
                onContinue = { navController.navigate(Routes.SIGNUP_CONTACTS) },
                onSkip = { navController.navigate(Routes.SIGNUP_CONTACTS) }
            )
        }

        // ---- Sign-up Step 4: contact information (skippable) ----
        composable(Routes.SIGNUP_CONTACTS) {
            ContactInfoScreen(
                onFinish = { finishSignUp(navController) },
                onSkip = { finishSignUp(navController) }
            )
        }

        // ---- Dashboard ----
        composable(
            route = Routes.DASHBOARD,
            arguments = listOf(
                navArgument("studentId") { type = NavType.StringType },
                navArgument("schoolId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: "there"
            val schoolId = backStackEntry.arguments?.getString("schoolId") ?: ""
            HomeScreen(
                studentId = studentId,
                schoolId = schoolId,
                onLogout = {
                    session.clearSession()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

/**
 * Creates the account from the sign-up draft and persists the session.
 * TODO: replace UserRepository with a Room-backed store when the DB lands.
 */
private fun registerAndLogin(session: SessionManager) {
    val studentId = SignUpDraft.studentId
    val schoolId = SignUpDraft.campusId
    // Register is idempotent-ish here: if it already exists we still log in.
    UserRepository.register(studentId, schoolId, SignUpDraft.password)
    session.saveLoginSession(studentId, schoolId)
}

/** Ends the flow: navigate to the dashboard for the newly created account. */
private fun finishSignUp(navController: NavHostController) {
    // Capture before clearing the draft.
    val studentId = SignUpDraft.studentId
    val schoolId = SignUpDraft.campusId
    SignUpDraft.clear()
    navController.navigate(Routes.dashboard(studentId, schoolId)) {
        // Clear the whole sign-up back stack.
        popUpTo(Routes.LOGIN) { inclusive = true }
    }
}
