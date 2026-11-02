package com.medkit.db

import java.sql.ResultSet
import java.util.UUID

/**
 * Mirrors the [Users] SQL table exactly. [passwordHash] is nullable because
 * OAuth-only accounts never have a password.
 */
data class UserRecord(
    val id:                UUID,
    val email:             String,
    val passwordHash:      String?,   // null for OAuth-only accounts
    val authProvider:      String,    // "local" | "google" | "microsoft"
    val providerSubjectId: String?    // OAuth subject/sub claim
)

/**
 * All SQL operations against the [Users] table.
 * Plain JDBC — no ORM — so every query is explicit and easy to audit.
 */
object UserQueries {

    // ── Reads ────────────────────────────────────────────────────────────────

    fun findByEmail(email: String): UserRecord? = Database.connect { conn ->
        conn.prepareStatement(
            "SELECT Id, Email, PasswordHash, AuthProvider, ProviderSubjectId " +
            "FROM Users WHERE Email = ?"
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
                "SELECT Id, Email, PasswordHash, AuthProvider, ProviderSubjectId " +
                "FROM Users WHERE AuthProvider = ? AND ProviderSubjectId = ?"
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
            "SELECT Id, Email, PasswordHash, AuthProvider, ProviderSubjectId " +
            "FROM Users WHERE Id = ?"
        ).use { stmt ->
            stmt.setString(1, id.toString())
            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.toUserRecord() else null
            }
        }
    }

    // ── Writes ───────────────────────────────────────────────────────────────

    /**
     * Inserts a new local (email + password) user.
     * The caller must pass an already-hashed password — plain text is never
     * written here.
     */
    fun insertLocal(email: String, passwordHash: String): UUID {
        val id = UUID.randomUUID()
        Database.connect { conn ->
            conn.prepareStatement(
                "INSERT INTO Users (Id, Email, PasswordHash, AuthProvider) " +
                "VALUES (?, ?, ?, 'local')"
            ).use { stmt ->
                stmt.setString(1, id.toString())
                stmt.setString(2, email.trim().lowercase())
                stmt.setString(3, passwordHash)
                stmt.executeUpdate()
            }
        }
        return id
    }

    /**
     * Inserts a new OAuth user (no password).
     * [provider] should be "google" or "microsoft".
     */
    fun insertOAuth(email: String, provider: String, subjectId: String): UUID {
        val id = UUID.randomUUID()
        Database.connect { conn ->
            conn.prepareStatement(
                "INSERT INTO Users (Id, Email, PasswordHash, AuthProvider, ProviderSubjectId) " +
                "VALUES (?, ?, NULL, ?, ?)"
            ).use { stmt ->
                stmt.setString(1, id.toString())
                stmt.setString(2, email.trim().lowercase())
                stmt.setString(3, provider)
                stmt.setString(4, subjectId)
                stmt.executeUpdate()
            }
        }
        return id
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun ResultSet.toUserRecord() = UserRecord(
        id                = UUID.fromString(getString("Id")),
        email             = getString("Email"),
        passwordHash      = getString("PasswordHash"),
        authProvider      = getString("AuthProvider"),
        providerSubjectId = getString("ProviderSubjectId")
    )
}
