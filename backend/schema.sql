-- ============================================================
-- MedKit PostgreSQL schema
-- Run this once against your Aiven PostgreSQL database.
-- ============================================================

-- ── Users ─────────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS users (
    id                  UUID        NOT NULL DEFAULT gen_random_uuid(),
    email               TEXT        NOT NULL,
    password_hash       TEXT,                           -- NULL for OAuth accounts
    auth_provider       TEXT        NOT NULL DEFAULT 'local',  -- local|google|microsoft
    provider_subject_id TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_users          PRIMARY KEY (id),
    CONSTRAINT uq_users_email    UNIQUE (email)
);

CREATE INDEX IF NOT EXISTS ix_users_email
    ON users(email);

CREATE UNIQUE INDEX IF NOT EXISTS ix_users_provider
    ON users(auth_provider, provider_subject_id)
    WHERE provider_subject_id IS NOT NULL;

-- ── Medical Profiles ──────────────────────────────────────────────────────────

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
    conditions  TEXT,       -- JSON array stored as text, e.g. ["Asthma"]
    medications TEXT,       -- JSON array stored as text
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_medical_profiles     PRIMARY KEY (id),
    CONSTRAINT uq_medical_profiles_user UNIQUE (user_id),
    CONSTRAINT fk_medical_profiles_users
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ── Email Verifications (OTP) ─────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS email_verifications (
    id         UUID        NOT NULL DEFAULT gen_random_uuid(),
    email      TEXT        NOT NULL,
    otp_hash   TEXT        NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    verified   BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_email_verifications PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS ix_email_verifications_email
    ON email_verifications(email);
