package com.example.kotlinkmpsetup.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinkmpsetup.domain.model.AppBanner
import com.example.kotlinkmpsetup.domain.model.AppNews
import com.example.kotlinkmpsetup.domain.model.NetworkResult
import com.example.kotlinkmpsetup.domain.model.ServiceItemModel
import com.example.kotlinkmpsetup.domain.model.WalletBalance
import com.example.kotlinkmpsetup.domain.repository.HomeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: PRESENTATION (Home UI State)
 * ====================================================================================
 */
data class HomeUiState(
    val balance: WalletBalance = WalletBalance(),
    val banners: List<AppBanner> = emptyList(),
    val news: AppNews = AppNews(),
    val services: List<ServiceItemModel> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: PRESENTATION (Home ViewModel)
 * ====================================================================================
 * Null-safe state management for Home Dashboard APIs (Balance, Banners, News, Services).
 */
class HomeViewModel(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData(isRefresh = false)
    }

    fun refreshData() {
        loadHomeData(isRefresh = true)
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun loadHomeData(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            try {
                val balanceDeferred = async { homeRepository.getBalance() }
                val bannersDeferred = async { homeRepository.getBanners() }
                val newsDeferred = async { homeRepository.getNews() }
                val servicesDeferred = async { homeRepository.getServices() }

                val balanceResult = balanceDeferred.await()
                val bannersResult = bannersDeferred.await()
                val newsResult = newsDeferred.await()
                val servicesResult = servicesDeferred.await()

                _uiState.update { currentState ->
                    var updated = currentState.copy(
                        isLoading = false,
                        isRefreshing = false
                    )

                    if (balanceResult is NetworkResult.Success) {
                        updated = updated.copy(balance = balanceResult.data)
                    }
                    if (bannersResult is NetworkResult.Success) {
                        updated = updated.copy(banners = bannersResult.data)
                    }
                    if (newsResult is NetworkResult.Success) {
                        updated = updated.copy(news = newsResult.data)
                    }
                    if (servicesResult is NetworkResult.Success) {
                        updated = updated.copy(services = servicesResult.data)
                    }

                    updated
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = e.localizedMessage ?: "Failed to refresh dashboard data"
                    )
                }
            }
        }
    }
}
