package com.medkit.db

import java.time.Instant
import java.util.UUID

/**
 * All SQL operations against the [EmailVerifications] table.
 *
 * OTPs are stored as BCrypt hashes — the plain-text code is never written
 * to the database. The [com.medkit.auth.PasswordService] is reused for
 * hash/verify so we don't pull in a second hashing library.
 *
 * Schema (run in SSMS before starting the server):
 *
 *   CREATE TABLE EmailVerifications (
 *       Id        UNIQUEIDENTIFIER NOT NULL DEFAULT NEWSEQUENTIALID(),
 *       Email     NVARCHAR(320)    NOT NULL,
 *       OtpHash   NVARCHAR(255)    NOT NULL,
 *       ExpiresAt DATETIME2        NOT NULL,
 *       Verified  BIT              NOT NULL DEFAULT 0,
 *       CreatedAt DATETIME2        NOT NULL DEFAULT SYSUTCDATETIME(),
 *       CONSTRAINT PK_EmailVerifications PRIMARY KEY (Id)
 *   );
 *   CREATE INDEX IX_EmailVerifications_Email ON EmailVerifications(Email);
 */
object OtpQueries {

    /**
     * Inserts a new OTP row.
     *
     * @param email     normalised (trimmed, lowercase) recipient email
     * @param otpHash   BCrypt hash of the plain-text 6-digit code
     * @param expiresAt when the code stops being valid
     */
    fun insertOtp(email: String, otpHash: String, expiresAt: Instant) {
        val id = UUID.randomUUID()
        Database.connect { conn ->
            conn.prepareStatement(
                """
                INSERT INTO EmailVerifications (Id, Email, OtpHash, ExpiresAt)
                VALUES (?, ?, ?, ?)
                """.trimIndent()
            ).use { stmt ->
                stmt.setString(1, id.toString())
                stmt.setString(2, email)
                stmt.setString(3, otpHash)
                // SQL Server JDBC accepts java.sql.Timestamp for DATETIME2
                stmt.setTimestamp(4, java.sql.Timestamp.from(expiresAt))
                stmt.executeUpdate()
            }
        }
    }

    /**
     * Returns the most recent unexpired, unverified OTP hash for [email],
     * or null if none exists.
     *
     * We always take the newest row so a resend overwrites the old code in
     * effect (old rows are left in the table but ignored).
     */
    fun findLatestPending(email: String): OtpRow? = Database.connect { conn ->
        conn.prepareStatement(
            """
            SELECT TOP 1 Id, OtpHash, ExpiresAt
            FROM EmailVerifications
            WHERE Email   = ?
              AND Verified = 0
              AND ExpiresAt > SYSUTCDATETIME()
            ORDER BY CreatedAt DESC
            """.trimIndent()
        ).use { stmt ->
            stmt.setString(1, email)
            stmt.executeQuery().use { rs ->
                if (rs.next()) {
                    OtpRow(
                        id        = UUID.fromString(rs.getString("Id")),
                        otpHash   = rs.getString("OtpHash"),
                        expiresAt = rs.getTimestamp("ExpiresAt").toInstant()
                    )
                } else null
            }
        }
    }

    /**
     * Marks the row with [id] as verified. Call this only after the hash has
     * been successfully verified in the service layer.
     */
    fun markVerified(id: UUID) {
        Database.connect { conn ->
            conn.prepareStatement(
                "UPDATE EmailVerifications SET Verified = 1 WHERE Id = ?"
            ).use { stmt ->
                stmt.setString(1, id.toString())
                stmt.executeUpdate()
            }
        }
    }

    /**
     * Returns true if there is at least one verified OTP row for [email].
     * Used by [/auth/register] to confirm the address was validated before
     * the account is created.
     */
    fun isEmailVerified(email: String): Boolean = Database.connect { conn ->
        conn.prepareStatement(
            "SELECT TOP 1 1 FROM EmailVerifications WHERE Email = ? AND Verified = 1"
        ).use { stmt ->
            stmt.setString(1, email)
            stmt.executeQuery().use { rs -> rs.next() }
        }
    }

    /**
     * Housekeeping: deletes all rows (verified or expired) for [email].
     * Called after a successful registration so the table doesn't accumulate
     * stale rows.
     */
    fun deleteForEmail(email: String) {
        Database.connect { conn ->
            conn.prepareStatement(
                "DELETE FROM EmailVerifications WHERE Email = ?"
            ).use { stmt ->
                stmt.setString(1, email)
                stmt.executeUpdate()
            }
        }
    }
}

/** Lightweight projection — only the fields the service layer needs. */
data class OtpRow(
    val id:        UUID,
    val otpHash:   String,
    val expiresAt: Instant
)
