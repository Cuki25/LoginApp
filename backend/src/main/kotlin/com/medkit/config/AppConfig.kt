package com.medkit.config

import io.github.cdimascio.dotenv.dotenv

/**
 * Loads every config value from the .env file (falling back to real environment
 * variables so this works in CI/production too).
 *
 * All lookups use [env] (a safe wrapper around both the .env file and real env
 * vars) so no value is ever null, even when a variable is simply absent.
 */
object AppConfig {

    /**
     * Merged config source: tries the .env file first, then falls back to the
     * real process environment. [ignoreIfMissing] prevents a crash when the
     * .env file doesn't exist (e.g. on Render).
     *
     * dotenv-kotlin v6 always reads real env vars as a fallback, so a single
     * [env] call covers both sources.
     */
    private val env = dotenv {
        ignoreIfMissing = true
        filename        = ".env"
    }

    /**
     * Safe lookup: checks the real environment first (so Render env vars win
     * over .env), then the dotenv file, then returns [default].
     * Never returns null, never throws.
     */
    private fun get(key: String, default: String = ""): String =
        System.getenv(key)?.takeIf { it.isNotBlank() }
            ?: runCatching { env[key] }.getOrNull()?.takeIf { it.isNotBlank() }
            ?: default

    // ── Database ──────────────────────────────────────────────────────────────
    val dbUrl:  String = get("DB_URL")
    val dbUser: String = get("DB_USER")
    val dbPass: String = get("DB_PASS")

    // ── JWT ───────────────────────────────────────────────────────────────────
    val jwtSecret:        String = get("JWT_SECRET")
    val jwtIssuer:        String = get("JWT_ISSUER")
    val jwtAudience:      String = get("JWT_AUDIENCE")
    val jwtExpiryMinutes: Long   = get("JWT_EXPIRY_MINUTES", "1440").toLong()

    // ── Server ────────────────────────────────────────────────────────────────
    // Render injects PORT; fall back to SERVER_PORT for local dev, then 8080.
    val serverPort: Int =
        get("PORT").toIntOrNull()
            ?: get("SERVER_PORT", "8080").toInt()

    // ── Google OAuth ──────────────────────────────────────────────────────────
    val googleClientId:     String = get("GOOGLE_CLIENT_ID")
    val googleClientSecret: String = get("GOOGLE_CLIENT_SECRET")
    val googleRedirectUri:  String = get(
        "GOOGLE_REDIRECT_URI", "http://localhost:8080/auth/google/callback"
    )

    // ── Microsoft OAuth ───────────────────────────────────────────────────────
    val microsoftClientId:     String = get("MICROSOFT_CLIENT_ID")
    val microsoftClientSecret: String = get("MICROSOFT_CLIENT_SECRET")
    val microsoftTenantId:     String = get("MICROSOFT_TENANT_ID", "common")
    val microsoftRedirectUri:  String = get(
        "MICROSOFT_REDIRECT_URI", "http://localhost:8080/auth/microsoft/callback"
    )

    // ── Resend email API ──────────────────────────────────────────────────────
    val resendApiKey:     String = get("RESEND_API_KEY")
    val resendFromEmail:  String = get("RESEND_FROM_EMAIL", "onboarding@resend.dev")
    val otpExpiryMinutes: Long   = get("OTP_EXPIRY_MINUTES", "10").toLong()
}
