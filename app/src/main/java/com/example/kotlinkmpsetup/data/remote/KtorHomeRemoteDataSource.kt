package com.example.kotlinkmpsetup.data.remote

import com.example.kotlinkmpsetup.data.remote.dto.ArsenBalanceResponseDto
import com.example.kotlinkmpsetup.data.remote.dto.ArsenBannerResponseDto
import com.example.kotlinkmpsetup.data.remote.dto.ArsenNewsResponseDto
import com.example.kotlinkmpsetup.data.remote.dto.ArsenServicesResponseDto
import com.example.kotlinkmpsetup.domain.model.AppBanner
import com.example.kotlinkmpsetup.domain.model.AppNews
import com.example.kotlinkmpsetup.domain.model.NetworkResult
import com.example.kotlinkmpsetup.domain.model.ServiceItemModel
import com.example.kotlinkmpsetup.domain.model.WalletBalance
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DATA (Ktor Home Remote Data Source)
 * ====================================================================================
 * Handles network calls for Home Dashboard APIs (GetBalance, GetAppBanner, GetAppNews, GetOpTypes)
 * wrapped in null-safe `safeApiCall`.
 */
class KtorHomeRemoteDataSource(
    private val client: HttpClient
) {

    suspend fun getBalance(): NetworkResult<WalletBalance> {
        val result = safeApiCall<ArsenBalanceResponseDto> {
            client.post(ApiEndpoints.Home.GET_BALANCE).body()
        }

        return when (result) {
            is NetworkResult.Success -> {
                val dto = result.data
                if (dto.statusCode == 1) {
                    val mainBal = dto.data?.balance ?: 0.0
                    val bankBal = dto.data?.bBalance ?: 0.0
                    NetworkResult.Success(
                        WalletBalance(
                            mainBalance = mainBal,
                            bankBalance = bankBal,
                            formattedMainBalance = "₹ ${String.format("%.2f", mainBal)}",
                            formattedBankBalance = "₹ ${String.format("%.2f", bankBal)}"
                        )
                    )
                } else {
                    NetworkResult.Error(dto.message ?: "Failed to fetch wallet balance")
                }
            }
            is NetworkResult.Error -> result
            is NetworkResult.NetworkOffline -> NetworkResult.NetworkOffline
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.Pending -> NetworkResult.Pending
        }
    }

    suspend fun getBanners(): NetworkResult<List<AppBanner>> {
        val result = safeApiCall<ArsenBannerResponseDto> {
            client.post(ApiEndpoints.Home.GET_APP_BANNER).body()
        }

        return when (result) {
            is NetworkResult.Success -> {
                val dto = result.data
                val bannersList = dto.banners?.mapNotNull { item ->
                    if (item.resourceUrl.isNull_or_blank_safe()) null
                    else AppBanner(
                        id = item.id ?: 0,
                        imageUrl = item.resourceUrl.orEmpty(),
                        redirectUrl = item.siteResourceUrl.orEmpty()
                    )
                }.orEmpty()
                NetworkResult.Success(bannersList)
            }
            is NetworkResult.Error -> result
            is NetworkResult.NetworkOffline -> NetworkResult.NetworkOffline
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.Pending -> NetworkResult.Pending
        }
    }

    suspend fun getNews(): NetworkResult<AppNews> {
        val result = safeApiCall<ArsenNewsResponseDto> {
            client.post(ApiEndpoints.Home.GET_APP_NEWS).body()
        }

        return when (result) {
            is NetworkResult.Success -> {
                val dto = result.data
                val rawNews = dto.newsContent?.newsDetail ?: dto.newsContent?.newsContent ?: dto.news.orEmpty()
                val cleanedNews = parseHtmlText(rawNews)
                NetworkResult.Success(
                    AppNews(
                        id = dto.newsContent?.id ?: 0,
                        content = cleanedNews
                    )
                )
            }
            is NetworkResult.Error -> result
            is NetworkResult.NetworkOffline -> NetworkResult.NetworkOffline
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.Pending -> NetworkResult.Pending
        }
    }

    suspend fun getServices(): NetworkResult<List<ServiceItemModel>> {
        val result = safeApiCall<ArsenServicesResponseDto> {
            client.post(ApiEndpoints.Home.GET_OP_TYPES).body()
        }

        return when (result) {
            is NetworkResult.Success -> {
                val dto = result.data
                val rawList = dto.data?.assignedOpTypes
                    ?: dto.data?.opTypes
                    ?: dto.assignedOpTypes
                    ?: dto.opTypes
                    ?: emptyList()

                val items = rawList.map { item ->
                    val active = item.isServiceActive == true && item.isActive == true

                    ServiceItemModel(
                        id = item.serviceID ?: 0,
                        parentId = item.parentID ?: 0,
                        name = item.name ?: item.service ?: "Service",
                        serviceType = item.service.orEmpty(),
                        sCode = item.sCode.orEmpty(),
                        iconName = item.icon.orEmpty(),
                        isEnabled = active,
                        categoryId = item.parentID ?: item.opTypeID ?: 0
                    )
                }
                NetworkResult.Success(items)
            }
            is NetworkResult.Error -> result
            is NetworkResult.NetworkOffline -> NetworkResult.NetworkOffline
            is NetworkResult.Loading -> NetworkResult.Loading
            is NetworkResult.Pending -> NetworkResult.Pending
        }
    }

    private fun parseHtmlText(html: String?): String {
        if (html.isNullOrBlank()) return ""
        return try {
            android.text.Html.fromHtml(html, android.text.Html.FROM_HTML_MODE_LEGACY).toString().trim()
        } catch (e: Exception) {
            html.replace("<[^>]*>".toRegex(), "").trim()
        }
    }

    private fun String?.isNull_or_blank_safe(): Boolean {
        return this == null || this.trim().isEmpty()
    }
}
