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

fun Route.authRoutes() {

    route("/auth") {

        // ── POST /auth/register ───────────────────────────────────────────────
        //
        // Body:  { "email": "...", "password": "..." }
        // 201:   { "token": "...", "email": "...", "userId": "..." }
        // 400:   { "error": "..." }   (validation failure / email already taken)
        post("/register") {
            val body = call.receive<RegisterRequest>()

            // ── Validate inputs ───────────────────────────────────────────────
            val email    = body.email.trim().lowercase()
            val password = body.password

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

            // ── Gate: email must have been verified before registration ──────
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

            // ── Check for duplicate email ─────────────────────────────────────
            if (UserQueries.findByEmail(email) != null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("An account with that email already exists.")
                )
                return@post
            }

            // ── Hash password and persist user ────────────────────────────────
            val hash   = PasswordService.hash(password)
            val userId = UserQueries.insertLocal(email, hash)

            // Create the linked (empty) medical profile row immediately so the
            // foreign key is satisfied before the client tries to fill it in.
            MedicalProfileQueries.createEmpty(userId)

            // Clean up the now-consumed OTP rows.
            OtpQueries.deleteForEmail(email)

            // ── Issue JWT ─────────────────────────────────────────────────────
            val token = JwtService.generateToken(userId, email)
            call.respond(
                HttpStatusCode.Created,
                AuthResponse(token = token, email = email, userId = userId.toString())
            )
        }

        // ── POST /auth/login ──────────────────────────────────────────────────
        //
        // Body:  { "email": "...", "password": "..." }
        // 200:   { "token": "...", "email": "...", "userId": "..." }
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

            // ── Look up user ──────────────────────────────────────────────────
            val user = UserQueries.findByEmail(email)
            if (user == null) {
                // Return a generic message — don't reveal whether the email exists.
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse("Invalid email or password.")
                )
                return@post
            }

            // ── Guard: account was created via OAuth, has no password ─────────
            val hash = user.passwordHash
            if (hash == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        "This account was created with ${user.authProvider.replaceFirstChar { it.uppercase() }}. " +
                        "Please sign in with that provider."
                    )
                )
                return@post
            }

            // ── Verify BCrypt hash ────────────────────────────────────────────
            if (!PasswordService.verify(password, hash)) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse("Invalid email or password.")
                )
                return@post
            }

            // ── Issue JWT ─────────────────────────────────────────────────────
            val token = JwtService.generateToken(user.id, email)
            call.respond(
                HttpStatusCode.OK,
                AuthResponse(token = token, email = email, userId = user.id.toString())
            )
        }
    }
}

// ── Tiny RFC-5322-ish email check (no external dependency needed) ─────────────
private fun String.isValidEmail(): Boolean =
    isNotBlank() && contains('@') && substringAfter('@').contains('.')
