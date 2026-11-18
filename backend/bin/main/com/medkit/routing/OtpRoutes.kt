package com.medkit.routing

import com.medkit.auth.OtpService
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
        //
        // The OTP code is always printed to stdout so it is visible in Render logs.
        // EmailService.sendOtp never throws — email failures are logged but do not
        // cause a 502, so development works even before Resend is configured.
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

            // Generate the code, hash it, and store in DB.
            val code = OtpService.generate(email)

            // Dispatch via Resend HTTPS API (non-throwing — logs on failure).
            EmailService.sendOtp(toEmail = email, code = code)

            call.respond(
                HttpStatusCode.OK,
                MessageResponse(
                    "Verification code sent to $email. " +
                    "It expires in ${com.medkit.config.AppConfig.otpExpiryMinutes} minutes."
                )
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

                OtpService.VerifyResult.Success -> call.respond(
                    HttpStatusCode.OK,
                    MessageResponse("Email verified. You can now complete your registration.")
                )

                OtpService.VerifyResult.AlreadyVerified -> call.respond(
                    HttpStatusCode.OK,
                    MessageResponse("Email already verified.")
                )

                OtpService.VerifyResult.InvalidOrExpired -> call.respond(
                    HttpStatusCode.UnprocessableEntity,
                    ErrorResponse("The code is incorrect or has expired. Please request a new one.")
                )
            }
        }
    }
}

private fun String.isValidEmail(): Boolean =
    isNotBlank() && contains('@') && substringAfter('@').contains('.')
