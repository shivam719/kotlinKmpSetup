package com.example.kotlinkmpsetup.domain.model

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DOMAIN (Home Domain Models)
 * ====================================================================================
 * Clean, null-safe domain models for Home Dashboard data:
 * - Wallet Balance
 * - Promo Banners
 * - News Announcements
 * - Quick Services
 */

data class WalletBalance(
    val mainBalance: Double = 0.0,
    val bankBalance: Double = 0.0,
    val formattedMainBalance: String = "₹ 0.00",
    val formattedBankBalance: String = "₹ 0.00"
)

data class AppBanner(
    val id: Int = 0,
    val imageUrl: String = "",
    val redirectUrl: String = ""
)

data class AppNews(
    val id: Int = 0,
    val content: String = "",
    val newsDetail: String = ""
)

data class ServiceItemModel(
    val id: Int = 0,
    val parentId: Int = 0,
    val name: String = "",
    val serviceType: String = "",
    val sCode: String = "",
    val iconName: String = "",
    val isEnabled: Boolean = true,
    val categoryId: Int = 0
)
