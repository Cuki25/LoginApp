package com.medkit.routing

import com.medkit.auth.JwtService
import com.medkit.auth.PasswordService
import com.medkit.db.MedicalProfileQueries
import com.medkit.db.OtpQueries
import com.medkit.db.UserQueries
import com.medkit.models.AuthResponse
import com.medkit.models.ErrorResponse
import com.medkit.models.LoginRequest
import com.medkit.models.RegisterRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/** STI student numbers: exactly 11 chars, start with 02000, six more digits. */
private val STUDENT_NUMBER_RE = Regex("^02000\\d{6}$")

fun Route.authRoutes() {

    route("/auth") {

        // ── POST /auth/register ───────────────────────────────────────────────
        //
        // Body:  { "email": "...", "password": "...", "studentNumber": "02000XXXXXX" }
        // 201:   { "token": "...", "email": "...", "userId": "...", "studentNumber": "..." }
        // 400:   { "error": "..." }
        // 403:   { "error": "..." }  (email not OTP-verified)
        post("/register") {
            val body = call.receive<RegisterRequest>()

            val email         = body.email.trim().lowercase()
            val password      = body.password
            val studentNumber = body.studentNumber.trim()

            // ── Validate inputs ───────────────────────────────────────────────
            if (!email.isValidEmail()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid email address."))
                return@post
            }
            if (password.length < 8) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Password must be at least 8 characters.")
                )
                return@post
            }
            if (studentNumber.isBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Student number is required.")
                )
                return@post
            }
            if (!STUDENT_NUMBER_RE.matches(studentNumber)) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Student number must be 11 digits starting with 02000 (e.g. 02000123456).")
                )
                return@post
            }

            // ── Gate: email must have been OTP-verified ───────────────────────
            if (!OtpQueries.isEmailVerified(email)) {
                call.respond(
                    HttpStatusCode.Forbidden,
                    ErrorResponse(
                        "Email address not verified. " +
                        "Please call /auth/send-otp and /auth/verify-otp first."
                    )
                )
                return@post
            }

            // ── Check for duplicates ──────────────────────────────────────────
            if (UserQueries.findByEmail(email) != null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("An account with that email already exists.")
                )
                return@post
            }
            if (UserQueries.findByStudentNumber(studentNumber) != null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("An account with that student number already exists.")
                )
                return@post
            }

            // ── Persist user ──────────────────────────────────────────────────
            val hash   = PasswordService.hash(password)
            val userId = UserQueries.insertLocal(email, hash, studentNumber)
            MedicalProfileQueries.createEmpty(userId)
            OtpQueries.deleteForEmail(email)

            val token = JwtService.generateToken(userId, email)
            call.respond(
                HttpStatusCode.Created,
                AuthResponse(
                    token         = token,
                    email         = email,
                    userId        = userId.toString(),
                    studentNumber = studentNumber
                )
            )
        }

        // ── POST /auth/login ──────────────────────────────────────────────────
        //
        // Body:  { "email": "...", "password": "..." }
        // 200:   { "token": "...", "email": "...", "userId": "...", "studentNumber": "..." }
        // 400/401: { "error": "..." }
        post("/login") {
            val body = call.receive<LoginRequest>()

            val email    = body.email.trim().lowercase()
            val password = body.password

            if (email.isBlank() || password.isBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Email and password are required.")
                )
                return@post
            }

            val user = UserQueries.findByEmail(email) ?: run {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse("Invalid email or password.")
                )
                return@post
            }

            val hash = user.passwordHash
            if (hash == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        "This account was created with ${
                            user.authProvider.replaceFirstChar { it.uppercase() }
                        }. Please sign in with that provider."
                    )
                )
                return@post
            }

            if (!PasswordService.verify(password, hash)) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse("Invalid email or password.")
                )
                return@post
            }

            val token = JwtService.generateToken(user.id, email)
            call.respond(
                HttpStatusCode.OK,
                AuthResponse(
                    token         = token,
                    email         = email,
                    userId        = user.id.toString(),
                    studentNumber = user.studentNumber
                )
            )
        }
    }
}

private fun String.isValidEmail(): Boolean =
    isNotBlank() && contains('@') && substringAfter('@').contains('.')
