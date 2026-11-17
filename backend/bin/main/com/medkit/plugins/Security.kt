package com.medkit.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.medkit.config.AppConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.http.*
import io.ktor.server.response.*

fun Application.configureSecurity() {
    authentication {
        jwt("jwt-auth") {
            realm = "MedKit API"
            verifier(
                JWT.require(Algorithm.HMAC256(AppConfig.jwtSecret))
                    .withIssuer(AppConfig.jwtIssuer)
                    .withAudience(AppConfig.jwtAudience)
                    .build()
            )
            validate { credential ->
                // Subject must be a non-blank user id
                val subject = credential.payload.subject
                if (!subject.isNullOrBlank()) JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("error" to "Token is invalid or expired.")
                )
            }
        }
    }
}
