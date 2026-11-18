package com.medkit.db

import java.sql.ResultSet
import java.util.UUID

/**
 * Mirrors the [users] PostgreSQL table.
 * [passwordHash] is nullable — OAuth-only accounts never have a password.
 * [studentNumber] follows the STI format: ^02000\d{6}$ (11 chars).
 */
data class UserRecord(
    val id:                UUID,
    val email:             String,
    val passwordHash:      String?,
    val authProvider:      String,
    val providerSubjectId: String?,
    val studentNumber:     String?  = null
)

object UserQueries {

    // ── Reads ─────────────────────────────────────────────────────────────────

    fun findByEmail(email: String): UserRecord? = Database.connect { conn ->
        conn.prepareStatement(
            "SELECT id, email, password_hash, auth_provider, provider_subject_id, student_number " +
            "FROM users WHERE email = ?"
        ).use { stmt ->
            stmt.setString(1, email.trim().lowercase())
            stmt.executeQuery().use { rs -> if (rs.next()) rs.toUserRecord() else null }
        }
    }

    fun findByStudentNumber(studentNumber: String): UserRecord? = Database.connect { conn ->
        conn.prepareStatement(
            "SELECT id, email, password_hash, auth_provider, provider_subject_id, student_number " +
            "FROM users WHERE student_number = ?"
        ).use { stmt ->
            stmt.setString(1, studentNumber)
            stmt.executeQuery().use { rs -> if (rs.next()) rs.toUserRecord() else null }
        }
    }

    fun findByProvider(provider: String, subjectId: String): UserRecord? =
        Database.connect { conn ->
            conn.prepareStatement(
                "SELECT id, email, password_hash, auth_provider, provider_subject_id, student_number " +
                "FROM users WHERE auth_provider = ? AND provider_subject_id = ?"
            ).use { stmt ->
                stmt.setString(1, provider)
                stmt.setString(2, subjectId)
                stmt.executeQuery().use { rs -> if (rs.next()) rs.toUserRecord() else null }
            }
        }

    fun findById(id: UUID): UserRecord? = Database.connect { conn ->
        conn.prepareStatement(
            "SELECT id, email, password_hash, auth_provider, provider_subject_id, student_number " +
            "FROM users WHERE id = ?"
        ).use { stmt ->
            stmt.setObject(1, id)
            stmt.executeQuery().use { rs -> if (rs.next()) rs.toUserRecord() else null }
        }
    }

    // ── Writes ────────────────────────────────────────────────────────────────

    fun insertLocal(email: String, passwordHash: String, studentNumber: String? = null): UUID {
        val id = UUID.randomUUID()
        Database.connect { conn ->
            conn.prepareStatement(
                "INSERT INTO users (id, email, password_hash, auth_provider, student_number) " +
                "VALUES (?, ?, ?, 'local', ?)"
            ).use { stmt ->
                stmt.setObject(1, id)
                stmt.setString(2, email.trim().lowercase())
                stmt.setString(3, passwordHash)
                if (studentNumber.isNullOrBlank()) stmt.setNull(4, java.sql.Types.VARCHAR)
                else stmt.setString(4, studentNumber)
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
        providerSubjectId = getString("provider_subject_id"),
        studentNumber     = getString("student_number")
    )
}
