package com.medkit.auth

import com.medkit.config.AppConfig
import com.medkit.db.OtpQueries
import com.medkit.db.OtpRow
import java.security.SecureRandom
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Handles OTP lifecycle: generation, storage, and verification.
 *
 * Security notes:
 *  - Codes are generated with [SecureRandom], not [java.util.Random].
 *  - The plain-text code is hashed with BCrypt before storage; the raw
 *    code is only ever held in memory long enough to be emailed.
 *  - Expiry is enforced both in the SQL query (ExpiresAt column) and
 *    checked again in [verify] as a defence-in-depth guard.
 *  - We don't distinguish "code not found" from "code expired" in the
 *    error message to avoid leaking information.
 */
object OtpService {

    private val random = SecureRandom()

    // ── Generate & store ──────────────────────────────────────────────────────

    /**
     * Generates a new 6-digit OTP, hashes it, writes it to the database,
     * and returns the **plain-text code** so [EmailService] can send it.
     *
     * The plain-text value is never persisted anywhere.
     */
    fun generate(email: String): String {
        val code      = String.format("%06d", random.nextInt(1_000_000))
        val hash      = PasswordService.hash(code)          // BCrypt
        val expiresAt = Instant.now().plus(
            AppConfig.otpExpiryMinutes, ChronoUnit.MINUTES
        )
        OtpQueries.insertOtp(email, hash, expiresAt)
        return code
    }

    // ── Verify ────────────────────────────────────────────────────────────────

    sealed interface VerifyResult {
        data object Success              : VerifyResult
        data object InvalidOrExpired     : VerifyResult
        data object AlreadyVerified      : VerifyResult
    }

    /**
     * Validates [code] against the latest pending OTP for [email].
     *
     * On success it marks the row as verified so the register endpoint
     * can confirm the address was validated.
     */
    fun verify(email: String, code: String): VerifyResult {
        // Short-circuit: if the email is already verified, no need to check again.
        // This also prevents replaying an old verified code to get a new JWT.
        if (OtpQueries.isEmailVerified(email)) return VerifyResult.AlreadyVerified

        val row: OtpRow = OtpQueries.findLatestPending(email)
            ?: return VerifyResult.InvalidOrExpired

        // Defence-in-depth: double-check expiry even though the SQL filters it.
        if (Instant.now().isAfter(row.expiresAt)) return VerifyResult.InvalidOrExpired

        // BCrypt comparison — same service used for passwords.
        if (!PasswordService.verify(code, row.otpHash)) return VerifyResult.InvalidOrExpired

        OtpQueries.markVerified(row.id)
        return VerifyResult.Success
    }
}
