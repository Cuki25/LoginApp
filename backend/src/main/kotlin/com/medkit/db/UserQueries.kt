package com.medkit.db

import java.sql.ResultSet
import java.util.UUID

/**
 * Mirrors the [users] PostgreSQL table.
 * [passwordHash] is nullable — OAuth-only accounts never have a password.
 *
 * PostgreSQL schema (run once against your Aiven DB):
 *
 *   CREATE TABLE users (
 *       id                  UUID        NOT NULL DEFAULT gen_random_uuid(),
 *       email               TEXT        NOT NULL,
 *       password_hash       TEXT,
 *       auth_provider       TEXT        NOT NULL DEFAULT 'local',
 *       provider_subject_id TEXT,
 *       created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
 *       CONSTRAINT pk_users            PRIMARY KEY (id),
 *       CONSTRAINT uq_users_email      UNIQUE (email),
 *       CONSTRAINT uq_users_provider   UNIQUE (auth_provider, provider_subject_id)
 *   );
 *   CREATE INDEX ix_users_email    ON users(email);
 *   CREATE INDEX ix_users_provider ON users(auth_provider, provider_subject_id)
 *       WHERE provider_subject_id IS NOT NULL;
 */
data class UserRecord(
    val id:                UUID,
    val email:             String,
    val passwordHash:      String?,
    val authProvider:      String,
    val providerSubjectId: String?
)

object UserQueries {

    // ── Reads ─────────────────────────────────────────────────────────────────

    fun findByEmail(email: String): UserRecord? = Database.connect { conn ->
        conn.prepareStatement(
            "SELECT id, email, password_hash, auth_provider, provider_subject_id " +
            "FROM users WHERE email = ?"
        ).use { stmt ->
            stmt.setString(1, email.trim().lowercase())
            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.toUserRecord() else null
            }
        }
    }

    fun findByProvider(provider: String, subjectId: String): UserRecord? =
        Database.connect { conn ->
            conn.prepareStatement(
                "SELECT id, email, password_hash, auth_provider, provider_subject_id " +
                "FROM users WHERE auth_provider = ? AND provider_subject_id = ?"
            ).use { stmt ->
                stmt.setString(1, provider)
                stmt.setString(2, subjectId)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) rs.toUserRecord() else null
                }
            }
        }

    fun findById(id: UUID): UserRecord? = Database.connect { conn ->
        conn.prepareStatement(
            "SELECT id, email, password_hash, auth_provider, provider_subject_id " +
            "FROM users WHERE id = ?"
        ).use { stmt ->
            stmt.setObject(1, id)   // PostgreSQL UUID type — use setObject, not setString
            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.toUserRecord() else null
            }
        }
    }

    // ── Writes ────────────────────────────────────────────────────────────────

    fun insertLocal(email: String, passwordHash: String): UUID {
        val id = UUID.randomUUID()
        Database.connect { conn ->
            conn.prepareStatement(
                "INSERT INTO users (id, email, password_hash, auth_provider) " +
                "VALUES (?, ?, ?, 'local')"
            ).use { stmt ->
                stmt.setObject(1, id)
                stmt.setString(2, email.trim().lowercase())
                stmt.setString(3, passwordHash)
                stmt.executeUpdate()
            }
        }
        return id
    }

    fun insertOAuth(email: String, provider: String, subjectId: String): UUID {
        val id = UUID.randomUUID()
        Database.connect { conn ->
            conn.prepareStatement(
                "INSERT INTO users (id, email, password_hash, auth_provider, provider_subject_id) " +
                "VALUES (?, ?, NULL, ?, ?)"
            ).use { stmt ->
                stmt.setObject(1, id)
                stmt.setString(2, email.trim().lowercase())
                stmt.setString(3, provider)
                stmt.setString(4, subjectId)
                stmt.executeUpdate()
            }
        }
        return id
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private fun ResultSet.toUserRecord() = UserRecord(
        id                = getObject("id", UUID::class.java),
        email             = getString("email"),
        passwordHash      = getString("password_hash"),
        authProvider      = getString("auth_provider"),
        providerSubjectId = getString("provider_subject_id")
    )
}
