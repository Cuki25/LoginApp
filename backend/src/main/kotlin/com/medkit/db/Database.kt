package com.medkit.db

import com.medkit.config.AppConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import java.sql.Connection

/**
 * Singleton that owns the HikariCP connection pool.
 *
 * Call [init] once at startup (Application.kt does this before the server
 * starts accepting requests). Every other layer calls [connect] to borrow
 * a connection from the pool — it is auto-closed at the end of the lambda
 * via Kotlin's `use`.
 */
object Database {

    private val log = LoggerFactory.getLogger(Database::class.java)
    private lateinit var dataSource: HikariDataSource

    fun init() {
        val cfg = HikariConfig().apply {
            jdbcUrl   = AppConfig.dbUrl
            // Only set credentials when using SQL Server Authentication.
            // For Windows Authentication the connection string already
            // contains integratedSecurity=true, so these stay blank.
            if (AppConfig.dbUser.isNotBlank()) username = AppConfig.dbUser
            if (AppConfig.dbPass.isNotBlank()) password = AppConfig.dbPass

            driverClassName          = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
            maximumPoolSize          = 10
            minimumIdle              = 2
            idleTimeout              = 600_000L   // 10 min
            connectionTimeout        = 30_000L    // 30 sec
            maxLifetime              = 1_800_000L // 30 min
            connectionTestQuery      = "SELECT 1"
        }
        dataSource = HikariDataSource(cfg)
        log.info("Database connection pool initialised (${AppConfig.dbUrl})")
    }

    /** Borrow a [Connection] from the pool. Auto-closed when the block exits. */
    fun <T> connect(block: (Connection) -> T): T = dataSource.connection.use(block)
}
