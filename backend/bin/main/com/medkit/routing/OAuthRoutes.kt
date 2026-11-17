package com.medkit.routing

import com.medkit.auth.JwtService
import com.medkit.config.AppConfig
import com.medkit.db.UserQueries
import com.medkit.models.AuthResponse
import com.medkit.models.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.net.URLEncoder

/**
 * OAuth 2.0 boilerplate for Google and Microsoft.
 *
 * HOW IT WORKS (Authorization Code flow):
 *
 *   1. Android app opens a Chrome Custom Tab to GET /auth/google  (or /microsoft)
 *   2. This server immediately redirects the browser to the provider's
 *      authorization URL with your client_id, scope, and redirect_uri.
 *   3. The user consents in the browser; the provider redirects back to
 *      GET /auth/google/callback?code=...
 *   4. This server exchanges the code for an id_token, validates it,
 *      upserts the user in SQL Server, and issues a MedKit JWT.
 *   5. The server redirects to the Android deep-link URI carrying the token
 *      so the app can close the Custom Tab and log the user in.
 *
 * WHAT IS A STUB vs REAL CODE:
 *   - The authorization redirect (steps 1-2) is FULLY IMPLEMENTED.
 *   - The token exchange and id_token parsing (steps 3-4) contain a clear
 *     TODO block. It needs an HTTP client call (e.g. ktor-client) to the
 *     provider's token endpoint plus JWT validation of the id_token.
 *     This is ~30 lines and is the only part you add when you inject real
 *     Client IDs.
 *
 * INJECT YOUR CLIENT IDs:
 *   Set GOOGLE_CLIENT_ID / MICROSOFT_CLIENT_ID in .env (or real env vars).
 *   The redirect URIs must be registered in each provider's console.
 */
fun Route.oauthRoutes() {

    route("/auth") {

        // ── Google ────────────────────────────────────────────────────────────

        // Step 1: redirect browser to Google's consent screen.
        get("/google") {
            if (AppConfig.googleClientId.isBlank() ||
                AppConfig.googleClientId == "YOUR_GOOGLE_CLIENT_ID"
            ) {
                call.respond(
                    HttpStatusCode.ServiceUnavailable,
                    ErrorResponse("Google OAuth is not configured yet. Set GOOGLE_CLIENT_ID in .env.")
                )
                return@get
            }
            val url = buildGoogleAuthUrl()
            call.respondRedirect(url, permanent = false)
        }

        // Step 2: Google redirects back here after user consents.
        get("/google/callback") {
            val code  = call.request.queryParameters["code"]
            val error = call.request.queryParameters["error"]

            if (error != null || code == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Google sign-in was cancelled or failed: $error")
                )
                return@get
            }

            // ── TODO: exchange code for tokens ────────────────────────────────
            //
            // 1. POST to https://oauth2.googleapis.com/token with:
            //      code, client_id, client_secret, redirect_uri, grant_type=authorization_code
            // 2. Parse the returned id_token (a JWT) — verify its signature
            //    using Google's public keys (https://www.googleapis.com/oauth2/v3/certs).
            // 3. Extract: sub (subject/provider ID), email, name from the payload.
            // 4. Replace the TODO block below with the real values.
            //
            // Recommended library: ktor-client-core + ktor-client-cio for the HTTP
            // call, and nimbus-jose-jwt for id_token verification.
            // ─────────────────────────────────────────────────────────────────

            val providerSubjectId = "TODO_GOOGLE_SUB"  // replace with real sub claim
            val email             = "TODO_GOOGLE_EMAIL" // replace with real email claim

            val medkitToken = upsertOAuthUserAndIssueToken(
                provider  = "google",
                subjectId = providerSubjectId,
                email     = email
            )

            if (medkitToken == null) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Could not create user account."))
                return@get
            }

            // Deep-link the token back to the Android app.
            call.respondRedirect("medkit://auth/callback?token=${medkitToken.token}&email=${medkitToken.email.encode()}")
        }

        // ── Microsoft ─────────────────────────────────────────────────────────

        // Step 1: redirect browser to Microsoft's consent screen.
        get("/microsoft") {
            if (AppConfig.microsoftClientId.isBlank() ||
                AppConfig.microsoftClientId == "YOUR_MICROSOFT_CLIENT_ID"
            ) {
                call.respond(
                    HttpStatusCode.ServiceUnavailable,
                    ErrorResponse("Microsoft OAuth is not configured yet. Set MICROSOFT_CLIENT_ID in .env.")
                )
                return@get
            }
            val url = buildMicrosoftAuthUrl()
            call.respondRedirect(url, permanent = false)
        }

        // Step 2: Microsoft redirects back here after user consents.
        get("/microsoft/callback") {
            val code  = call.request.queryParameters["code"]
            val error = call.request.queryParameters["error"]

            if (error != null || code == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Microsoft sign-in was cancelled or failed: $error")
                )
                return@get
            }

            // ── TODO: exchange code for tokens ────────────────────────────────
            //
            // 1. POST to https://login.microsoftonline.com/{tenant}/oauth2/v2.0/token
            //      with: code, client_id, client_secret, redirect_uri,
            //            grant_type=authorization_code, scope=openid profile email
            // 2. Parse the returned id_token JWT.
            //    Verify with Microsoft's JWKS: https://login.microsoftonline.com/common/discovery/v2.0/keys
            // 3. Extract: sub (oid is more stable for Microsoft), email.
            // 4. Replace the TODO block below.
            // ─────────────────────────────────────────────────────────────────

            val providerSubjectId = "TODO_MICROSOFT_OID"
            val email             = "TODO_MICROSOFT_EMAIL"

            val medkitToken = upsertOAuthUserAndIssueToken(
                provider  = "microsoft",
                subjectId = providerSubjectId,
                email     = email
            )

            if (medkitToken == null) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Could not create user account."))
                return@get
            }

            call.respondRedirect("medkit://auth/callback?token=${medkitToken.token}&email=${medkitToken.email.encode()}")
        }
    }
}

// ── URL builders ─────────────────────────────────────────────────────────────

private fun buildGoogleAuthUrl(): String {
    val base = "https://accounts.google.com/o/oauth2/v2/auth"
    return "$base?" + mapOf(
        "client_id"     to AppConfig.googleClientId,
        "redirect_uri"  to AppConfig.googleRedirectUri,
        "response_type" to "code",
        "scope"         to "openid email profile",
        "access_type"   to "offline",
        "prompt"        to "select_account"
    ).toQueryString()
}

private fun buildMicrosoftAuthUrl(): String {
    val tenant = AppConfig.microsoftTenantId
    val base   = "https://login.microsoftonline.com/$tenant/oauth2/v2.0/authorize"
    return "$base?" + mapOf(
        "client_id"     to AppConfig.microsoftClientId,
        "redirect_uri"  to AppConfig.microsoftRedirectUri,
        "response_type" to "code",
        "scope"         to "openid email profile",
        "prompt"        to "select_account"
    ).toQueryString()
}

// ── Shared helper: find-or-create OAuth user, issue MedKit JWT ───────────────

private fun upsertOAuthUserAndIssueToken(
    provider:  String,
    subjectId: String,
    email:     String
): AuthResponse? {
    // Check if this OAuth identity already has a MedKit account.
    val existing = UserQueries.findByProvider(provider, subjectId)
    val userId = if (existing != null) {
        existing.id
    } else {
        // New OAuth user — create the account + empty medical profile.
        val id = UserQueries.insertOAuth(email, provider, subjectId)
        com.medkit.db.MedicalProfileQueries.createEmpty(id)
        id
    }
    val token = JwtService.generateToken(userId, email)
    return AuthResponse(token = token, email = email, userId = userId.toString())
}

// ── Utility extensions ────────────────────────────────────────────────────────

private fun Map<String, String>.toQueryString(): String =
    entries.joinToString("&") { (k, v) -> "${k.encode()}=${v.encode()}" }

private fun String.encode(): String = URLEncoder.encode(this, "UTF-8")
