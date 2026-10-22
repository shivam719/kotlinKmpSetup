package com.example.kotlinkmpsetup.domain.repository

import com.example.kotlinkmpsetup.domain.model.AppBanner
import com.example.kotlinkmpsetup.domain.model.AppNews
import com.example.kotlinkmpsetup.domain.model.NetworkResult
import com.example.kotlinkmpsetup.domain.model.ServiceItemModel
import com.example.kotlinkmpsetup.domain.model.WalletBalance

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DOMAIN (Home Repository Interface)
 * ====================================================================================
 */
interface HomeRepository {
    suspend fun getBalance(): NetworkResult<WalletBalance>
    suspend fun getBanners(): NetworkResult<List<AppBanner>>
    suspend fun getNews(): NetworkResult<AppNews>
    suspend fun getServices(): NetworkResult<List<ServiceItemModel>>
}
