package com.medkit.auth

import org.mindrot.jbcrypt.BCrypt

/**
 * Thin wrapper around jBCrypt so the rest of the codebase never imports BCrypt
 * directly. A cost factor of 12 gives ~300 ms on modern hardware — strong
 * enough to resist brute-force without being painful for legitimate logins.
 */
object PasswordService {

    private const val BCRYPT_COST = 12

    /** Returns the BCrypt hash of [plaintext]. Store this; never store the raw password. */
    fun hash(plaintext: String): String = BCrypt.hashpw(plaintext, BCrypt.gensalt(BCRYPT_COST))

    /** Returns true if [plaintext] matches the stored [hash]. */
    fun verify(plaintext: String, hash: String): Boolean =
        runCatching { BCrypt.checkpw(plaintext, hash) }.getOrDefault(false)
}
