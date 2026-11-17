package com.medkit.db

import com.medkit.config.AppConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import java.sql.Connection

/**
 * Singleton that owns the HikariCP connection pool.
 *
 * Targets Aiven PostgreSQL. The full JDBC URL (including host, port,
 * database name, sslmode, user, and password) is read from the DB_URL
 * environment variable so no credentials are hard-coded.
 *
 * Call [init] once at startup (Application.kt does this before the server
 * starts accepting requests). Every other layer calls [connect] to borrow
 * a connection from the pool — it is auto-closed at the end of the lambda.
 */
object Database {

    private val log = LoggerFactory.getLogger(Database::class.java)
    private lateinit var dataSource: HikariDataSource

    fun init() {
        val cfg = HikariConfig().apply {
            jdbcUrl         = AppConfig.dbUrl
            driverClassName = "org.postgresql.Driver"

            // DB_USER / DB_PASS are optional — Aiven embeds credentials in the
            // JDBC URL itself (user=...&password=...). Only override when the URL
            // does not contain them.
            if (AppConfig.dbUser.isNotBlank()) username = AppConfig.dbUser
            if (AppConfig.dbPass.isNotBlank()) password = AppConfig.dbPass

            maximumPoolSize     = 10
            minimumIdle         = 2
            idleTimeout         = 600_000L    // 10 min
            connectionTimeout   = 30_000L     // 30 sec
            maxLifetime         = 1_800_000L  // 30 min
            connectionTestQuery = "SELECT 1"
        }
        dataSource = HikariDataSource(cfg)
        log.info("PostgreSQL connection pool initialised (${AppConfig.dbUrl.substringBefore('?')})")
    }

    /** Borrow a [Connection] from the pool. Auto-closed when the block exits. */
    fun <T> connect(block: (Connection) -> T): T = dataSource.connection.use(block)
}
