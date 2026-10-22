package com.example.kotlinkmpsetup.domain.model

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DOMAIN (Sealed Auth Result Hierarchy)
 * ====================================================================================
 * Type alias linking legacy [AuthResult] to the unified [NetworkResult] architecture.
 *
 * INTERVIEW POINT:
 * `typealias` provides zero-cost compile-time alias mapping, allowing domain logic
 * to use specialized names while maintaining unified state handling underlyingly.
 */
typealias AuthResult<T> = NetworkResult<T>
