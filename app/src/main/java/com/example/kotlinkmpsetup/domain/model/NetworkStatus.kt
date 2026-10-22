package com.example.kotlinkmpsetup.domain.model

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DOMAIN (Real-Time Network Status Model)
 * ====================================================================================
 * Represents the device's connectivity state (Online / Offline).
 *
 * INTERVIEW POINT:
 * Real-time network monitoring via StateFlow gives instant feedback when a user loses
 * internet connectivity, preventing failed API calls and offering offline UI banners.
 */
enum class NetworkStatus {
    Available,
    Unavailable,
    Losing,
    Lost
}
