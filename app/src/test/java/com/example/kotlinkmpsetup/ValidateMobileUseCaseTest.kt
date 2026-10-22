package com.example.kotlinkmpsetup

import com.example.kotlinkmpsetup.domain.usecase.ValidateMobileUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit Tests for [ValidateMobileUseCase] to verify mobile sanitization & edge cases.
 */
class ValidateMobileUseCaseTest {

    private val validator = ValidateMobileUseCase()

    @Test
    fun `mobile starting with 91 and 10 digits is NOT stripped`() {
        val input = "9145789658"
        val sanitized = ValidateMobileUseCase.sanitizeMobileNumber(input)
        val result = validator(input)

        assertEquals("9145789658", sanitized)
        assertTrue(result.isValid)
    }

    @Test
    fun `mobile with plus 91 prefix is stripped`() {
        val input = "+919145789658"
        val sanitized = ValidateMobileUseCase.sanitizeMobileNumber(input)
        val result = validator(input)

        assertEquals("9145789658", sanitized)
        assertTrue(result.isValid)
    }

    @Test
    fun `mobile with 91 prefix and 12 digits is stripped`() {
        val input = "919145789658"
        val sanitized = ValidateMobileUseCase.sanitizeMobileNumber(input)
        val result = validator(input)

        assertEquals("9145789658", sanitized)
        assertTrue(result.isValid)
    }

    @Test
    fun `mobile with 0 prefix and 11 digits is stripped`() {
        val input = "09145789658"
        val sanitized = ValidateMobileUseCase.sanitizeMobileNumber(input)
        val result = validator(input)

        assertEquals("9145789658", sanitized)
        assertTrue(result.isValid)
    }

    @Test
    fun `mobile starting with invalid digit 5 fails validation`() {
        val input = "5145789658"
        val result = validator(input)

        assertFalse(result.isValid)
        assertEquals("Mobile number must start with 6, 7, 8, or 9", result.errorMessage)
    }
}
