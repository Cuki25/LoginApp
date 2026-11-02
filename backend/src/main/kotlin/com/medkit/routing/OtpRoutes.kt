package com.medkit.routing

import com.medkit.auth.OtpService
import com.medkit.email.EmailException
import com.medkit.email.EmailService
import com.medkit.models.ErrorResponse
import com.medkit.models.MessageResponse
import com.medkit.models.SendOtpRequest
import com.medkit.models.VerifyOtpRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("OtpRoutes")

fun Route.otpRoutes() {

    route("/auth") {

        // ── POST /auth/send-otp ───────────────────────────────────────────────
        //
        // Body:  { "email": "..." }
        // 200:   { "message": "Verification code sent to ..." }
        // 400:   { "error": "..." }   (missing / invalid email)
        // 502:   { "error": "..." }   (SMTP failure — SMTP not configured, etc.)
        //
        // Rate-limiting: the table stores every OTP insert, but only the
        // most recent unexpired row is checked on verify. Repeated calls
        // generate a new code, effectively invalidating the previous one.
        // For production, add an IP- or email-based rate limiter here.
        post("/send-otp") {
            val body  = call.receive<SendOtpRequest>()
            val email = body.email.trim().lowercase()

            if (!email.isValidEmail()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("A valid email address is required.")
                )
                return@post
            }

            // Generate the code and store its hash in the DB.
            val code = OtpService.generate(email)

            // Send the email — catch SMTP errors so the HTTP layer stays clean.
            try {
                EmailService.sendOtp(toEmail = email, code = code)
            } catch (e: EmailException) {
                log.error("SMTP failure for {}: {}", email, e.message)
                call.respond(
                    HttpStatusCode.BadGateway,
                    ErrorResponse(
                        "Could not send the verification email. " +
                        "Check that SMTP_EMAIL and SMTP_PASSWORD are set in .env."
                    )
                )
                return@post
            }

            call.respond(
                HttpStatusCode.OK,
                MessageResponse("Verification code sent to $email. It expires in ${com.medkit.config.AppConfig.otpExpiryMinutes} minutes.")
            )
        }

        // ── POST /auth/verify-otp ─────────────────────────────────────────────
        //
        // Body:  { "email": "...", "code": "123456" }
        // 200:   { "message": "Email verified." }
        // 400:   { "error": "..." }   (bad input)
        // 422:   { "error": "..." }   (wrong / expired code)
        post("/verify-otp") {
            val body  = call.receive<VerifyOtpRequest>()
            val email = body.email.trim().lowercase()
            val code  = body.code.trim()

            if (!email.isValidEmail()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("A valid email address is required.")
                )
                return@post
            }
            if (code.length != 6 || code.any { !it.isDigit() }) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Code must be exactly 6 digits.")
                )
                return@post
            }

            when (OtpService.verify(email, code)) {

                OtpService.VerifyResult.Success -> {
                    call.respond(
                        HttpStatusCode.OK,
                        MessageResponse("Email verified. You can now complete your registration.")
                    )
                }

                OtpService.VerifyResult.AlreadyVerified -> {
                    // Idempotent — treat as success so the client can retry safely.
                    call.respond(
                        HttpStatusCode.OK,
                        MessageResponse("Email already verified.")
                    )
                }

                OtpService.VerifyResult.InvalidOrExpired -> {
                    // 422 Unprocessable — the request was well-formed but the code
                    // didn't pass validation. The Android client should show this
                    // message and let the user request a new code.
                    call.respond(
                        HttpStatusCode.UnprocessableEntity,
                        ErrorResponse("The code is incorrect or has expired. Please request a new one.")
                    )
                }
            }
        }
    }
}

// ── Reused from AuthRoutes — keep in sync or extract to a shared util ─────────
private fun String.isValidEmail(): Boolean =
    isNotBlank() && contains('@') && substringAfter('@').contains('.')
