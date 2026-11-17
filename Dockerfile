# ─────────────────────────────────────────────────────────────────────────────
# Stage 1 — build
# Runs ./gradlew :backend:installDist to produce the self-contained distribution
# under backend/build/install/backend/.
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk AS build

WORKDIR /src

# Copy only the files Gradle needs to resolve dependencies first (better layer
# caching — the heavyweight download step is skipped on subsequent builds when
# only source files change).
COPY gradlew gradlew
COPY gradle/ gradle/
COPY settings.gradle.kts settings.gradle.kts
COPY build.gradle.kts build.gradle.kts
COPY backend/ backend/

RUN chmod +x gradlew && \
    ./gradlew :backend:installDist --no-daemon

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2 — runtime
# Copies only the distribution artefact — no JDK, no Gradle, no source.
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre

WORKDIR /app

# installDist always creates one directory whose name matches the subproject
# ("backend"). Copy it to a fixed path so the CMD doesn't need to know the name.
COPY --from=build /src/backend/build/install/backend/ /app/install/

EXPOSE 8080

# Use exec form via sh -c so the shell can expand the glob, but Netty still
# receives SIGTERM directly thanks to exec.
CMD ["sh", "-c", "exec /app/install/bin/backend"]
