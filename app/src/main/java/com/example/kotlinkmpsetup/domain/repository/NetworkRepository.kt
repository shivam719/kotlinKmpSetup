package com.example.kotlinkmpsetup.domain.repository

import com.example.kotlinkmpsetup.domain.model.NetworkStatus
import kotlinx.coroutines.flow.Flow

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DOMAIN (Network Repository Contract)
 * ====================================================================================
 * Exposes real-time connectivity status flow.
 *
 * INTERVIEW POINT:
 * Using Kotlin Flow for network observation allows reactive UI updates. The UI can display
 * an "Offline Banner" immediately when connectivity drops, preventing invalid API calls.
 */
interface NetworkRepository {
    fun observeNetworkStatus(): Flow<NetworkStatus>
    fun isConnected(): Boolean
}
