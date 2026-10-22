package com.example.kotlinkmpsetup.domain.model

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DOMAIN (Real-Time Form Validation)
 * ====================================================================================
 * Result object returned by input validation Use Cases.
 *
 * INTERVIEW POINT:
 * Placing input validation logic inside Domain UseCases (e.g. `ValidateEmailUseCase`)
 * guarantees that validation rules are identical across UI screens and unit tests,
 * and prevents ViewModel clutter.
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
