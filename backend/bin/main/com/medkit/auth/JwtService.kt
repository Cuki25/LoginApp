package com.medkit.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.medkit.config.AppConfig
import java.util.Date
import java.util.UUID

/**
 * Creates signed JWT tokens. Verification lives in [com.medkit.plugins.Security].
 */
object JwtService {

    private val algorithm = Algorithm.HMAC256(AppConfig.jwtSecret)

    /**
     * Issues a new token for [userId].
     *
     * Claims included:
     *  - sub  → userId (UUID string) — used as the principal by Ktor's auth plugin
     *  - email → the user's email address (convenience claim for the client)
     */
    fun generateToken(userId: UUID, email: String): String {
        val expiresAt = Date(
            System.currentTimeMillis() + AppConfig.jwtExpiryMinutes * 60 * 1_000L
        )
        return JWT.create()
            .withIssuer(AppConfig.jwtIssuer)
            .withAudience(AppConfig.jwtAudience)
            .withSubject(userId.toString())
            .withClaim("email", email)
            .withIssuedAt(Date())
            .withExpiresAt(expiresAt)
            .sign(algorithm)
    }
}
