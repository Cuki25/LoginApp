plugins {
    kotlin("jvm")
    id("io.ktor.plugin") version "2.3.12"
    kotlin("plugin.serialization") version "1.9.24"
}

group = "com.medkit"
version = "0.0.1"

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.medkit.ApplicationKt")
    val isDevelopment = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

val ktorVersion = "2.3.12"
val kotlinVersion = "1.9.24"

dependencies {
    // Ktor server
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")

    // Auth — JWT verification inside Ktor
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktorVersion")

    // JWT token creation
    implementation("com.auth0:java-jwt:4.4.0")

    // Password hashing — BCrypt
    implementation("org.mindrot:jbcrypt:0.4")

    // PostgreSQL JDBC driver
    implementation("org.postgresql:postgresql:42.7.4")

    // HikariCP — connection pooling
    implementation("com.zaxxer:HikariCP:5.1.0")

    // .env file loading for local secrets
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // JSON serialisation — used by EmailService to build the Resend payload
    implementation("com.google.code.gson:gson:2.10.1")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // Test
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}
