package com.example.kotlinkmpsetup.data.repository

import com.example.kotlinkmpsetup.data.network.NetworkConnectivityObserver
import com.example.kotlinkmpsetup.domain.model.NetworkStatus
import com.example.kotlinkmpsetup.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DATA (Network Repository Implementation)
 * ====================================================================================
 * Implements `NetworkRepository` using `NetworkConnectivityObserver`.
 */
class NetworkRepositoryImpl(
    private val connectivityObserver: NetworkConnectivityObserver
) : NetworkRepository {

    override fun observeNetworkStatus(): Flow<NetworkStatus> {
        return connectivityObserver.observe()
    }

    override fun isConnected(): Boolean {
        return connectivityObserver.isCurrentlyConnected()
    }
}
