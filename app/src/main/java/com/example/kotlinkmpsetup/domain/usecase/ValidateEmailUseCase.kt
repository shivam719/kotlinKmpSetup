package com.example.kotlinkmpsetup.domain.usecase

import android.util.Patterns
import com.example.kotlinkmpsetup.domain.model.ValidationResult

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DOMAIN (Use Case - Single Responsibility Principle)
 * ====================================================================================
 * Validates Email input format in real-time.
 *
 * INTERVIEW POINT:
 * Use Cases (Interactors) represent single reusable business rules. By isolating email
 * validation inside `ValidateEmailUseCase`, we can test it independently with 100% code coverage.
 */
class ValidateEmailUseCase {
    operator fun invoke(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Email address cannot be empty"
            )
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Please enter a valid email address"
            )
        }
        return ValidationResult(isValid = true)
    }
}
