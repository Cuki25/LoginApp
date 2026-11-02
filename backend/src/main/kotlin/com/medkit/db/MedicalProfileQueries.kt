package com.medkit.db

import java.sql.ResultSet
import java.util.UUID

/**
 * Mirrors the [MedicalProfiles] SQL table.
 * [conditions] and [medications] are stored as JSON arrays in NVARCHAR(MAX)
 * columns; they are returned as raw strings here and serialised/deserialised
 * in the service layer.
 */
data class MedicalProfileRecord(
    val id:          UUID,
    val userId:      UUID,
    val campusId:    String?,
    val studentId:   String?,
    val firstName:   String?,
    val lastName:    String?,
    val middleName:  String?,
    val birthday:    String?,   // ISO-8601 date string "YYYY-MM-DD"
    val gender:      String?,
    val bloodType:   String?,
    val allergies:   String?,
    val weightKg:    Double?,
    val heightCm:    Double?,
    val conditions:  String?,   // JSON array e.g. ["Asthma","Hypertension"]
    val medications: String?    // JSON array e.g. ["Metformin 500mg"]
)

/**
 * All SQL operations against the [MedicalProfiles] table.
 *
 * TODO (Phase 2): add updateProfile() and full read-by-userId.
 */
object MedicalProfileQueries {

    fun findByUserId(userId: UUID): MedicalProfileRecord? = Database.connect { conn ->
        conn.prepareStatement(
            """
            SELECT Id, UserId, CampusId, StudentId, FirstName, LastName,
                   MiddleName, Birthday, Gender, BloodType, Allergies,
                   WeightKg, HeightCm, Conditions, Medications
            FROM MedicalProfiles
            WHERE UserId = ?
            """.trimIndent()
        ).use { stmt ->
            stmt.setString(1, userId.toString())
            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.toRecord() else null
            }
        }
    }

    /**
     * Creates an empty profile row linked to [userId].
     * Called immediately after a new user registers so the foreign-key row
     * always exists. Fields are filled in later via [upsertProfile].
     */
    fun createEmpty(userId: UUID): UUID {
        val id = UUID.randomUUID()
        Database.connect { conn ->
            conn.prepareStatement(
                "INSERT INTO MedicalProfiles (Id, UserId) VALUES (?, ?)"
            ).use { stmt ->
                stmt.setString(1, id.toString())
                stmt.setString(2, userId.toString())
                stmt.executeUpdate()
            }
        }
        return id
    }

    /**
     * Upserts every editable field for the profile belonging to [userId].
     * Uses a MERGE statement so callers don't need to know if the row exists.
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
            MERGE MedicalProfiles AS target
            USING (SELECT CAST(? AS UNIQUEIDENTIFIER) AS UserId) AS src
              ON target.UserId = src.UserId
            WHEN MATCHED THEN UPDATE SET
                CampusId   = ?, StudentId  = ?, FirstName  = ?, LastName   = ?,
                MiddleName = ?, Birthday   = ?, Gender     = ?, BloodType  = ?,
                Allergies  = ?, WeightKg   = ?, HeightCm   = ?,
                Conditions = ?, Medications = ?, UpdatedAt = SYSUTCDATETIME()
            WHEN NOT MATCHED THEN INSERT
                (Id, UserId, CampusId, StudentId, FirstName, LastName,
                 MiddleName, Birthday, Gender, BloodType, Allergies,
                 WeightKg, HeightCm, Conditions, Medications)
            VALUES
                (NEWID(), CAST(? AS UNIQUEIDENTIFIER),
                 ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """.trimIndent()
        ).use { stmt ->
            // WHEN MATCHED params (1..15)
            stmt.setString(1, userId.toString())
            stmt.setString(2,  campusId)
            stmt.setString(3,  studentId)
            stmt.setString(4,  firstName)
            stmt.setString(5,  lastName)
            stmt.setString(6,  middleName)
            stmt.setString(7,  birthday)
            stmt.setString(8,  gender)
            stmt.setString(9,  bloodType)
            stmt.setString(10, allergies)
            if (weightKg != null) stmt.setDouble(11, weightKg)
            else stmt.setNull(11, java.sql.Types.DECIMAL)
            if (heightCm != null) stmt.setDouble(12, heightCm)
            else stmt.setNull(12, java.sql.Types.DECIMAL)
            stmt.setString(13, conditions)
            stmt.setString(14, medications)
            // WHEN NOT MATCHED params (15..29)
            stmt.setString(15, userId.toString())
            stmt.setString(16, campusId)
            stmt.setString(17, studentId)
            stmt.setString(18, firstName)
            stmt.setString(19, lastName)
            stmt.setString(20, middleName)
            stmt.setString(21, birthday)
            stmt.setString(22, gender)
            stmt.setString(23, bloodType)
            stmt.setString(24, allergies)
            if (weightKg != null) stmt.setDouble(25, weightKg)
            else stmt.setNull(25, java.sql.Types.DECIMAL)
            if (heightCm != null) stmt.setDouble(26, heightCm)
            else stmt.setNull(26, java.sql.Types.DECIMAL)
            stmt.setString(27, conditions)
            stmt.setString(28, medications)
            stmt.executeUpdate()
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private fun ResultSet.toRecord() = MedicalProfileRecord(
        id          = UUID.fromString(getString("Id")),
        userId      = UUID.fromString(getString("UserId")),
        campusId    = getString("CampusId"),
        studentId   = getString("StudentId"),
        firstName   = getString("FirstName"),
        lastName    = getString("LastName"),
        middleName  = getString("MiddleName"),
        birthday    = getString("Birthday"),
        gender      = getString("Gender"),
        bloodType   = getString("BloodType"),
        allergies   = getString("Allergies"),
        weightKg    = getDouble("WeightKg").takeIf { !wasNull() },
        heightCm    = getDouble("HeightCm").takeIf { !wasNull() },
        conditions  = getString("Conditions"),
        medications = getString("Medications")
    )
}
