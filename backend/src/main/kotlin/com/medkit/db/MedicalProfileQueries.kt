package com.medkit.db

import java.sql.ResultSet
import java.util.UUID

/**
 * Mirrors the [medical_profiles] PostgreSQL table.
 * [conditions] and [medications] are stored as TEXT (JSON arrays as strings).
 *
 * PostgreSQL schema (run once against your Aiven DB):
 *
 *   CREATE TABLE medical_profiles (
 *       id          UUID        NOT NULL DEFAULT gen_random_uuid(),
 *       user_id     UUID        NOT NULL,
 *       campus_id   TEXT,
 *       student_id  TEXT,
 *       first_name  TEXT,
 *       last_name   TEXT,
 *       middle_name TEXT,
 *       birthday    DATE,
 *       gender      TEXT,
 *       blood_type  TEXT,
 *       allergies   TEXT,
 *       weight_kg   NUMERIC(5,2),
 *       height_cm   NUMERIC(5,2),
 *       conditions  TEXT,
 *       medications TEXT,
 *       updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
 *       CONSTRAINT pk_medical_profiles    PRIMARY KEY (id),
 *       CONSTRAINT fk_medical_profiles_users
 *           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
 *       CONSTRAINT uq_medical_profiles_user UNIQUE (user_id)
 *   );
 *
 * The UNIQUE constraint on user_id lets the INSERT … ON CONFLICT upsert work.
 */
data class MedicalProfileRecord(
    val id:          UUID,
    val userId:      UUID,
    val campusId:    String?,
    val studentId:   String?,
    val firstName:   String?,
    val lastName:    String?,
    val middleName:  String?,
    val birthday:    String?,
    val gender:      String?,
    val bloodType:   String?,
    val allergies:   String?,
    val weightKg:    Double?,
    val heightCm:    Double?,
    val conditions:  String?,
    val medications: String?
)

object MedicalProfileQueries {

    fun findByUserId(userId: UUID): MedicalProfileRecord? = Database.connect { conn ->
        conn.prepareStatement(
            """
            SELECT id, user_id, campus_id, student_id, first_name, last_name,
                   middle_name, birthday, gender, blood_type, allergies,
                   weight_kg, height_cm, conditions, medications
            FROM   medical_profiles
            WHERE  user_id = ?
            """.trimIndent()
        ).use { stmt ->
            stmt.setObject(1, userId)
            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.toRecord() else null
            }
        }
    }

    /**
     * Creates an empty profile row for [userId].
     * Uses ON CONFLICT DO NOTHING so it is safe to call more than once.
     */
    fun createEmpty(userId: UUID): UUID {
        val id = UUID.randomUUID()
        Database.connect { conn ->
            conn.prepareStatement(
                "INSERT INTO medical_profiles (id, user_id) VALUES (?, ?) " +
                "ON CONFLICT (user_id) DO NOTHING"
            ).use { stmt ->
                stmt.setObject(1, id)
                stmt.setObject(2, userId)
                stmt.executeUpdate()
            }
        }
        return id
    }

    /**
     * Upserts every editable field for [userId].
     *
     * PostgreSQL equivalent of T-SQL MERGE: INSERT … ON CONFLICT (user_id) DO UPDATE.
     * Requires the UNIQUE constraint on user_id (see schema above).
     */
    fun upsertProfile(
        userId:      UUID,
        campusId:    String?,
        studentId:   String?,
        firstName:   String?,
        lastName:    String?,
        middleName:  String?,
        birthday:    String?,
        gender:      String?,
        bloodType:   String?,
        allergies:   String?,
        weightKg:    Double?,
        heightCm:    Double?,
        conditions:  String?,
        medications: String?
    ) = Database.connect { conn ->
        conn.prepareStatement(
            """
            INSERT INTO medical_profiles
                (id, user_id, campus_id, student_id, first_name, last_name,
                 middle_name, birthday, gender, blood_type, allergies,
                 weight_kg, height_cm, conditions, medications, updated_at)
            VALUES
                (gen_random_uuid(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())
            ON CONFLICT (user_id) DO UPDATE SET
                campus_id   = EXCLUDED.campus_id,
                student_id  = EXCLUDED.student_id,
                first_name  = EXCLUDED.first_name,
                last_name   = EXCLUDED.last_name,
                middle_name = EXCLUDED.middle_name,
                birthday    = EXCLUDED.birthday,
                gender      = EXCLUDED.gender,
                blood_type  = EXCLUDED.blood_type,
                allergies   = EXCLUDED.allergies,
                weight_kg   = EXCLUDED.weight_kg,
                height_cm   = EXCLUDED.height_cm,
                conditions  = EXCLUDED.conditions,
                medications = EXCLUDED.medications,
                updated_at  = now()
            """.trimIndent()
        ).use { stmt ->
            stmt.setObject(1, userId)
            stmt.setString(2, campusId)
            stmt.setString(3, studentId)
            stmt.setString(4, firstName)
            stmt.setString(5, lastName)
            stmt.setString(6, middleName)
            stmt.setString(7, birthday)
            stmt.setString(8, gender)
            stmt.setString(9, bloodType)
            stmt.setString(10, allergies)
            if (weightKg != null) stmt.setDouble(11, weightKg)
            else stmt.setNull(11, java.sql.Types.NUMERIC)
            if (heightCm != null) stmt.setDouble(12, heightCm)
            else stmt.setNull(12, java.sql.Types.NUMERIC)
            stmt.setString(13, conditions)
            stmt.setString(14, medications)
            stmt.executeUpdate()
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private fun ResultSet.toRecord() = MedicalProfileRecord(
        id          = getObject("id",        UUID::class.java),
        userId      = getObject("user_id",   UUID::class.java),
        campusId    = getString("campus_id"),
        studentId   = getString("student_id"),
        firstName   = getString("first_name"),
        lastName    = getString("last_name"),
        middleName  = getString("middle_name"),
        birthday    = getString("birthday"),
        gender      = getString("gender"),
        bloodType   = getString("blood_type"),
        allergies   = getString("allergies"),
        weightKg    = getDouble("weight_kg").takeIf { !wasNull() },
        heightCm    = getDouble("height_cm").takeIf { !wasNull() },
        conditions  = getString("conditions"),
        medications = getString("medications")
    )
}
