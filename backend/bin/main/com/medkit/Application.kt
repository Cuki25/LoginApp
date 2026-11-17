package com.medkit

import com.medkit.config.AppConfig
import com.medkit.db.Database
import com.medkit.plugins.configureHTTP
import com.medkit.plugins.configureSecurity
import com.medkit.plugins.configureSerialization
import com.medkit.routing.configureRouting
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    // Initialise the connection pool before the server starts accepting requests.
    Database.init()

    embeddedServer(
        factory  = Netty,
        port     = AppConfig.serverPort,
        host     = "0.0.0.0",
        module   = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureSerialization()   // JSON content negotiation
    configureHTTP()            // CORS + global error handling
    configureSecurity()        // JWT bearer-token validation
    configureRouting()         // all API routes
}
