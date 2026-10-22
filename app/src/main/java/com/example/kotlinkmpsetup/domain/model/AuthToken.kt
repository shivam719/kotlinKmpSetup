package com.example.kotlinkmpsetup.domain.model

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DOMAIN (Enterprise Authentication Model)
 * ====================================================================================
 * Represents Bearer Access Token & Refresh Token pair used for secure API authorization.
 *
 * INTERVIEW POINT:
 * Bearer Tokens are attached to outgoing HTTP requests via `Authorization: Bearer <accessToken>`.
 * When an Access Token expires (HTTP 401 Unauthorized), Ktor's Auth plugin intercepts the error,
 * uses the `refreshToken` to request a new `accessToken`, and retries the original request seamlessly.
 */
data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresInSeconds: Long = 3600
)
