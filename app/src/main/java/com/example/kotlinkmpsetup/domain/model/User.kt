package com.example.kotlinkmpsetup.domain.model

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DOMAIN (Enterprise Business Model)
 * ====================================================================================
 * Represents the User Entity in the Domain Layer.
 *
 * INTERVIEW POINT:
 * In Clean Architecture, Domain entities are purely written in Kotlin and MUST NOT depend
 * on Android framework, Ktor DTOs, or database libraries. This keeps core business logic
 * decoupled, testable, and reusable across KMP (Kotlin Multiplatform) targets.
 */
data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val token: AuthToken
)
