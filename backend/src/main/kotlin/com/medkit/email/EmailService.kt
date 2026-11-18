package com.medkit.email

import com.medkit.config.AppConfig
import org.slf4j.LoggerFactory
import java.net.HttpURLConnection
import java.net.URL

/**
 * Sends transactional emails via the Resend HTTPS API.
 *
 * Configuration (set in .env or as Render environment variables):
 *   RESEND_API_KEY    — API key from https://resend.com/api-keys
 *   RESEND_FROM_EMAIL — verified sender address, e.g. "MedKit <noreply@yourdomain.com>"
 *                       Defaults to "onboarding@resend.dev" (Resend's shared test domain)
 *                       which works before a custom domain is verified.
 *
 * The OTP code is always printed to stdout so it can be read in Render logs
 * during testing even if the API call fails.
 */
object EmailService {

    private val log = LoggerFactory.getLogger(EmailService::class.java)
    private const val RESEND_URL = "https://api.resend.com/emails"

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Dispatches a MedKit OTP email via Resend.
     *
     * Never throws — errors are logged but swallowed so the calling route can
     * still respond 200 (the OTP is readable in server logs for testing).
     */
    fun sendOtp(toEmail: String, code: String) {
        // Always log so the code is readable in Render logs during testing.
        println("[OTP] $toEmail -> $code")
        log.info("[OTP] {} -> {}", toEmail, code)

        val apiKey = AppConfig.resendApiKey
        if (apiKey.isBlank()) {
            log.warn("[OTP] RESEND_API_KEY is not set — email not sent, but code is logged above.")
            return
        }

        val from    = AppConfig.resendFromEmail
        val subject = "Your MedKit verification code"
        val html    = buildOtpHtml(code)

        val payload = """{"from":"$from","to":["$toEmail"],"subject":"$subject","html":${
            com.google.gson.Gson().toJson(html)
        }}"""

        try {
            val conn = (URL(RESEND_URL).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput      = true
                connectTimeout = 15_000
                readTimeout    = 15_000
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Authorization", "Bearer $apiKey")
            }
            conn.outputStream.use { it.write(payload.toByteArray(Charsets.UTF_8)) }

            val status = conn.responseCode
            if (status in 200..299) {
                log.info("[OTP] Resend accepted email to {} (HTTP {})", toEmail, status)
            } else {
                val body = runCatching {
                    conn.errorStream?.bufferedReader()?.readText() ?: "(no body)"
                }.getOrDefault("(read error)")
                log.error("[OTP] Resend rejected email to {} — HTTP {}: {}", toEmail, status, body)
            }
        } catch (e: Exception) {
            log.error("[OTP] Failed to call Resend API for {}: {}", toEmail, e.message)
        }
    }

    // ── Email body ────────────────────────────────────────────────────────────

    private fun buildOtpHtml(code: String): String = """
        <div style="font-family:sans-serif;max-width:480px;margin:0 auto">
          <h2 style="color:#0D5C4A">Your MedKit verification code</h2>
          <p style="font-size:36px;font-weight:bold;letter-spacing:8px;color:#0D5C4A">$code</p>
          <p>This code expires in <strong>${AppConfig.otpExpiryMinutes} minutes</strong>.</p>
          <p style="color:#888;font-size:12px">If you didn't request this, you can safely ignore this email.</p>
        </div>
    """.trimIndent()
}

/** Kept for OtpRoutes backward-compat — no longer thrown. */
class EmailException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)
