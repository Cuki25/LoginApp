package com.medkit.db

import java.time.Instant
import java.util.UUID

/**
 * All SQL operations against the [email_verifications] table.
 *
 * OTPs are stored as BCrypt hashes — the plain-text code is never written
 * to the database.
 *
 * PostgreSQL schema (run once against your Aiven DB):
 *
 *   CREATE TABLE email_verifications (
 *       id          UUID        NOT NULL DEFAULT gen_random_uuid(),
 *       email       TEXT        NOT NULL,
 *       otp_hash    TEXT        NOT NULL,
 *       expires_at  TIMESTAMPTZ NOT NULL,
 *       verified    BOOLEAN     NOT NULL DEFAULT FALSE,
 *       created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
 *       CONSTRAINT pk_email_verifications PRIMARY KEY (id)
 *   );
 *   CREATE INDEX ix_email_verifications_email ON email_verifications(email);
 */
object OtpQueries {

    fun insertOtp(email: String, otpHash: String, expiresAt: Instant) {
        val id = UUID.randomUUID()
        Database.connect { conn ->
            conn.prepareStatement(
                "INSERT INTO email_verifications (id, email, otp_hash, expires_at) " +
                "VALUES (?, ?, ?, ?)"
            ).use { stmt ->
                stmt.setObject(1, id)
                stmt.setString(2, email)
                stmt.setString(3, otpHash)
                // PostgreSQL JDBC accepts OffsetDateTime or Timestamp for TIMESTAMPTZ
                stmt.setObject(4, expiresAt.atOffset(java.time.ZoneOffset.UTC))
                stmt.executeUpdate()
            }
        }
    }

    /**
     * Returns the most recent unexpired, unverified OTP row for [email].
     * PostgreSQL: ORDER BY … LIMIT 1 replaces T-SQL's TOP 1.
     */
    fun findLatestPending(email: String): OtpRow? = Database.connect { conn ->
        conn.prepareStatement(
            """
            SELECT id, otp_hash, expires_at
            FROM   email_verifications
            WHERE  email      = ?
              AND  verified   = FALSE
              AND  expires_at > now()
            ORDER BY created_at DESC
            LIMIT 1
            """.trimIndent()
        ).use { stmt ->
            stmt.setString(1, email)
            stmt.executeQuery().use { rs ->
                if (rs.next()) {
                    OtpRow(
                        id        = rs.getObject("id", UUID::class.java),
                        otpHash   = rs.getString("otp_hash"),
                        expiresAt = rs.getObject("expires_at",
                                        java.time.OffsetDateTime::class.java).toInstant()
                    )
                } else null
            }
        }
    }

    fun markVerified(id: UUID) {
        Database.connect { conn ->
            conn.prepareStatement(
                "UPDATE email_verifications SET verified = TRUE WHERE id = ?"
            ).use { stmt ->
                stmt.setObject(1, id)
                stmt.executeUpdate()
            }
        }
    }

    fun isEmailVerified(email: String): Boolean = Database.connect { conn ->
        conn.prepareStatement(
            "SELECT 1 FROM email_verifications WHERE email = ? AND verified = TRUE LIMIT 1"
        ).use { stmt ->
            stmt.setString(1, email)
            stmt.executeQuery().use { rs -> rs.next() }
        }
    }

    fun deleteForEmail(email: String) {
        Database.connect { conn ->
            conn.prepareStatement(
                "DELETE FROM email_verifications WHERE email = ?"
            ).use { stmt ->
                stmt.setString(1, email)
                stmt.executeUpdate()
            }
        }
    }
}

data class OtpRow(
    val id:        UUID,
    val otpHash:   String,
    val expiresAt: Instant
)
