package com.medkit.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Single entry-point for all routes, called from [com.medkit.Application.module].
 * Add new route groups here as the API grows.
 */
fun Application.configureRouting() {
    routing {
        // Render (and any load-balancer) calls this to decide if the instance is live.
        get("/health") {
            call.respondText("OK", ContentType.Text.Plain, HttpStatusCode.OK)
        }

        authRoutes()    // POST /auth/register, POST /auth/login
        otpRoutes()     // POST /auth/send-otp, POST /auth/verify-otp
        oauthRoutes()   // GET  /auth/google,   GET  /auth/microsoft (+ callbacks)
    }
}
