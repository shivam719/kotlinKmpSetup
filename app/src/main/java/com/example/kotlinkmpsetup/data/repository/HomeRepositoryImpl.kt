package com.example.kotlinkmpsetup.data.repository

import com.example.kotlinkmpsetup.data.remote.KtorHomeRemoteDataSource
import com.example.kotlinkmpsetup.domain.model.AppBanner
import com.example.kotlinkmpsetup.domain.model.AppNews
import com.example.kotlinkmpsetup.domain.model.NetworkResult
import com.example.kotlinkmpsetup.domain.model.ServiceItemModel
import com.example.kotlinkmpsetup.domain.model.WalletBalance
import com.example.kotlinkmpsetup.domain.repository.HomeRepository

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DATA (Home Repository Implementation)
 * ====================================================================================
 */
class HomeRepositoryImpl(
    private val remoteDataSource: KtorHomeRemoteDataSource
) : HomeRepository {

    override suspend fun getBalance(): NetworkResult<WalletBalance> = remoteDataSource.getBalance()

    override suspend fun getBanners(): NetworkResult<List<AppBanner>> = remoteDataSource.getBanners()

    override suspend fun getNews(): NetworkResult<AppNews> = remoteDataSource.getNews()

    override suspend fun getServices(): NetworkResult<List<ServiceItemModel>> = remoteDataSource.getServices()
}
