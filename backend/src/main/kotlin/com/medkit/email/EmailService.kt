package com.medkit.email

import com.medkit.config.AppConfig
import jakarta.mail.Message
import jakarta.mail.MessagingException
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.slf4j.LoggerFactory
import java.util.Properties

/**
 * Sends transactional emails via Gmail SMTP with STARTTLS on port 587.
 *
 * Configuration (set these in .env):
 *   SMTP_EMAIL    — your Gmail address (e.g. medkit.noreply@gmail.com)
 *   SMTP_PASSWORD — a Google App Password (NOT your Gmail login password)
 *                   Generate at: https://myaccount.google.com/apppasswords
 *                   Requires 2-Step Verification on your Google account.
 *
 * The service object creates a new [Session] per call — Sessions are
 * lightweight and thread-safe; keeping a long-lived one risks stale
 * connections with Gmail's idle timeouts.
 */
object EmailService {

    private val log = LoggerFactory.getLogger(EmailService::class.java)

    private const val SMTP_HOST = "smtp.gmail.com"
    private const val SMTP_PORT = "587"

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Sends a MedKit OTP verification email to [toEmail].
     *
     * @throws EmailException if the SMTP credentials are missing or the send fails.
     */
    fun sendOtp(toEmail: String, code: String) {
        val subject = "Your MedKit verification code"
        val body = buildOtpEmailBody(code)
        send(to = toEmail, subject = subject, body = body)
    }

    // ── Internal send ─────────────────────────────────────────────────────────

    private fun send(to: String, subject: String, body: String) {
        val fromEmail = AppConfig.smtpEmail
        val password  = AppConfig.smtpPassword

        if (fromEmail.isBlank() || password.isBlank()) {
            throw EmailException(
                "SMTP credentials are not configured. " +
                "Set SMTP_EMAIL and SMTP_PASSWORD in .env."
            )
        }

        val props = Properties().apply {
            put("mail.smtp.auth",                 "true")
            put("mail.smtp.starttls.enable",      "true")   // STARTTLS upgrade
            put("mail.smtp.starttls.required",    "true")   // refuse plain connections
            put("mail.smtp.host",                 SMTP_HOST)
            put("mail.smtp.port",                 SMTP_PORT)
            put("mail.smtp.ssl.trust",            SMTP_HOST)
            put("mail.smtp.connectiontimeout",    "10000")  // 10 s connect timeout
            put("mail.smtp.timeout",              "10000")  // 10 s read timeout
            put("mail.smtp.writetimeout",         "10000")  // 10 s write timeout
        }

        val session = Session.getInstance(props, object : jakarta.mail.Authenticator() {
            override fun getPasswordAuthentication() =
                PasswordAuthentication(fromEmail, password)
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(fromEmail, "MedKit"))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
                setSubject(subject, "UTF-8")
                // Plain-text fallback only; upgrade to MimeMultipart for HTML if needed.
                setText(body, "UTF-8")
            }
            Transport.send(message)
            log.info("OTP email sent to {}", to)
        } catch (e: MessagingException) {
            log.error("Failed to send email to {}: {}", to, e.message)
            throw EmailException("Failed to send verification email: ${e.message}", e)
        }
    }

    // ── Email body ────────────────────────────────────────────────────────────

    private fun buildOtpEmailBody(code: String): String = """
        Hi,

        Your MedKit verification code is:

            $code

        This code expires in ${AppConfig.otpExpiryMinutes} minutes.

        If you didn't request this, you can safely ignore this email.

        — The MedKit Team
    """.trimIndent()
}

/** Wraps SMTP errors so callers can catch a typed exception. */
class EmailException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)
