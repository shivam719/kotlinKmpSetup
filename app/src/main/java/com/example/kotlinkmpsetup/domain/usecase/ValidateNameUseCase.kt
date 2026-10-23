package com.example.kotlinkmpsetup.domain.usecase

import com.example.kotlinkmpsetup.domain.model.ValidationResult

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DOMAIN (Use Case - Full Name Validation)
 * ====================================================================================
 * Validates Full Name input dynamically based on selected language.
 */
class ValidateNameUseCase {
    operator fun invoke(fullName: String, isHindi: Boolean = false): ValidationResult {
        if (fullName.isBlank()) {
            return ValidationResult(
                isValid = false,
                errorMessage = if (isHindi) "पूरा नाम दर्ज करें" else "Please enter full name"
            )
        }
        if (fullName.trim().length < 2) {
            return ValidationResult(
                isValid = false,
                errorMessage = if (isHindi) "नाम कम से कम 2 अक्षरों का होना चाहिए" else "Full name must be at least 2 characters"
            )
        }
        return ValidationResult(isValid = true)
    }
}
