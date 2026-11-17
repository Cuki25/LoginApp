package com.medkit.config

import io.github.cdimascio.dotenv.dotenv

/**
 * Loads every config value from the .env file (falling back to real environment
 * variables so this works in CI/production too).
 */
object AppConfig {

    private val env = dotenv {
        ignoreIfMissing = true   // no crash when deployed with real env vars
        filename = ".env"
    }

    // ── Database ─────────────────────────────────────────────────────────────
    val dbUrl:  String = env["DB_URL"]
    val dbUser: String = env.getOrDefault("DB_USER", "")
    val dbPass: String = env.getOrDefault("DB_PASS", "")

    // ── JWT ───────────────────────────────────────────────────────────────────
    val jwtSecret:        String = env["JWT_SECRET"]
    val jwtIssuer:        String = env["JWT_ISSUER"]
    val jwtAudience:      String = env["JWT_AUDIENCE"]
    val jwtExpiryMinutes: Long   = env.getOrDefault("JWT_EXPIRY_MINUTES", "1440").toLong()

    // ── Server ────────────────────────────────────────────────────────────────
    // Render injects PORT; fall back to SERVER_PORT for local dev, then 8080.
    val serverPort: Int =
        System.getenv("PORT")?.toIntOrNull()
            ?: env.getOrDefault("SERVER_PORT", "8080").toInt()

    // ── Google OAuth ─────────────────────────────────────────────────────────
    val googleClientId:     String = env.getOrDefault("GOOGLE_CLIENT_ID", "")
    val googleClientSecret: String = env.getOrDefault("GOOGLE_CLIENT_SECRET", "")
    val googleRedirectUri:  String = env.getOrDefault(
        "GOOGLE_REDIRECT_URI", "http://localhost:8080/auth/google/callback"
    )

    // ── Microsoft OAuth ───────────────────────────────────────────────────────
    val microsoftClientId:     String = env.getOrDefault("MICROSOFT_CLIENT_ID", "")
    val microsoftClientSecret: String = env.getOrDefault("MICROSOFT_CLIENT_SECRET", "")
    val microsoftTenantId:     String = env.getOrDefault("MICROSOFT_TENANT_ID", "common")
    val microsoftRedirectUri:  String = env.getOrDefault(
        "MICROSOFT_REDIRECT_URI", "http://localhost:8080/auth/microsoft/callback"
    )

    // ── SMTP ──────────────────────────────────────────────────────────────────
    val smtpEmail:        String = env.getOrDefault("SMTP_EMAIL", "")
    val smtpPassword:     String = env.getOrDefault("SMTP_PASSWORD", "")
    val otpExpiryMinutes: Long   = env.getOrDefault("OTP_EXPIRY_MINUTES", "10").toLong()

    // ── Helpers ───────────────────────────────────────────────────────────────
    private fun io.github.cdimascio.dotenv.Dotenv.getOrDefault(key: String, default: String): String =
        runCatching { this[key] }.getOrDefault(default)
}
