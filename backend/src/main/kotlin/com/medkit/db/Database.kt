package com.medkit.db

import com.medkit.auth.PasswordService
import com.medkit.config.AppConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.util.UUID

/**
 * Singleton that owns the HikariCP connection pool.
 *
 * [init] must be called once at startup. It:
 *   1. Opens the pool.
 *   2. Creates all tables with CREATE TABLE IF NOT EXISTS.
 *   3. Runs safe ALTER TABLE … ADD COLUMN IF NOT EXISTS migrations.
 *   4. Seeds the demo student account (ON CONFLICT DO NOTHING).
 */
object Database {

    private val log = LoggerFactory.getLogger(Database::class.java)
    private lateinit var dataSource: HikariDataSource

    // ── Startup ───────────────────────────────────────────────────────────────

    fun init() {
        val cfg = HikariConfig().apply {
            jdbcUrl         = AppConfig.dbUrl
            driverClassName = "org.postgresql.Driver"
            if (AppConfig.dbUser.isNotBlank()) username = AppConfig.dbUser
            if (AppConfig.dbPass.isNotBlank()) password = AppConfig.dbPass
            maximumPoolSize     = 10
            minimumIdle         = 2
            idleTimeout         = 600_000L
            connectionTimeout   = 30_000L
            maxLifetime         = 1_800_000L
            connectionTestQuery = "SELECT 1"
        }
        dataSource = HikariDataSource(cfg)
        log.info("PostgreSQL pool initialised ({})", AppConfig.dbUrl.substringBefore('?'))

        connect { conn ->
            createTables(conn)
            runMigrations(conn)
            seedDemoUser(conn)
        }
    }

    // ── Pool access ───────────────────────────────────────────────────────────

    /** Borrow a [Connection] from the pool. Auto-closed when the block exits. */
    fun <T> connect(block: (Connection) -> T): T = dataSource.connection.use(block)

    // ── Schema creation ───────────────────────────────────────────────────────

    private fun createTables(conn: Connection) {
        conn.createStatement().use { stmt ->

            // ── users ─────────────────────────────────────────────────────────
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id                  UUID        NOT NULL DEFAULT gen_random_uuid(),
                    email               TEXT        NOT NULL,
                    password_hash       TEXT,
                    auth_provider       TEXT        NOT NULL DEFAULT 'local',
                    provider_subject_id TEXT,
                    student_number      VARCHAR(11),
                    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
                    CONSTRAINT pk_users            PRIMARY KEY (id),
                    CONSTRAINT uq_users_email      UNIQUE (email),
                    CONSTRAINT uq_users_student_no UNIQUE (student_number),
                    CONSTRAINT uq_users_provider   UNIQUE (auth_provider, provider_subject_id)
                )
            """.trimIndent())

            // ── medical_profiles ──────────────────────────────────────────────
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS medical_profiles (
                    id          UUID        NOT NULL DEFAULT gen_random_uuid(),
                    user_id     UUID        NOT NULL,
                    campus_id   TEXT,
                    student_id  TEXT,
                    first_name  TEXT,
                    last_name   TEXT,
                    middle_name TEXT,
                    birthday    DATE,
                    gender      TEXT,
                    blood_type  TEXT,
                    allergies   TEXT,
                    weight_kg   NUMERIC(5,2),
                    height_cm   NUMERIC(5,2),
                    conditions  TEXT,
                    medications TEXT,
                    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
                    CONSTRAINT pk_medical_profiles       PRIMARY KEY (id),
                    CONSTRAINT fk_medical_profiles_users
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    CONSTRAINT uq_medical_profiles_user  UNIQUE (user_id)
                )
            """.trimIndent())

            // ── email_verifications ───────────────────────────────────────────
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS email_verifications (
                    id         UUID        NOT NULL DEFAULT gen_random_uuid(),
                    email      TEXT        NOT NULL,
                    otp_hash   TEXT        NOT NULL,
                    expires_at TIMESTAMPTZ NOT NULL,
                    verified   BOOLEAN     NOT NULL DEFAULT FALSE,
                    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                    CONSTRAINT pk_email_verifications PRIMARY KEY (id)
                )
            """.trimIndent())

            stmt.execute(
                "CREATE INDEX IF NOT EXISTS ix_ev_email ON email_verifications(email)"
            )
            stmt.execute(
                "CREATE INDEX IF NOT EXISTS ix_users_email ON users(email)"
            )
        }
        log.info("Tables verified / created.")
    }

    // ── Migrations (idempotent) ───────────────────────────────────────────────

    private fun runMigrations(conn: Connection) {
        conn.createStatement().use { stmt ->
            // Add student_number if the table existed before this feature.
            stmt.execute(
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS " +
                "student_number VARCHAR(11)"
            )
            // Add unique constraint if missing (safe to run again — will error
            // only if a duplicate exists, not if the constraint already exists).
            runCatching {
                stmt.execute(
                    "ALTER TABLE users ADD CONSTRAINT uq_users_student_no " +
                    "UNIQUE (student_number)"
                )
            }
        }
        log.info("Migrations applied.")
    }

    // ── Demo user seed ────────────────────────────────────────────────────────

    private fun seedDemoUser(conn: Connection) {
        // BCrypt hash of "demo1234"
        val demoHash = PasswordService.hash("demo1234")
        val demoId   = UUID.nameUUIDFromBytes("demo-student-001".toByteArray())

        conn.prepareStatement("""
            INSERT INTO users (id, email, password_hash, auth_provider, student_number)
            VALUES (?, 'demo@taguig.edu.ph', ?, 'local', '02000000001')
            ON CONFLICT (email) DO NOTHING
        """.trimIndent()).use { stmt ->
            stmt.setObject(1, demoId)
            stmt.setString(2, demoHash)
            stmt.executeUpdate()
        }

        // Ensure the demo user has a medical profile row.
        conn.prepareStatement("""
            INSERT INTO medical_profiles (id, user_id)
            VALUES (gen_random_uuid(), ?)
            ON CONFLICT (user_id) DO NOTHING
        """.trimIndent()).use { stmt ->
            stmt.setObject(1, demoId)
            stmt.executeUpdate()
        }

        log.info("Demo user seeded (demo@taguig.edu.ph / demo1234).")
    }
}
