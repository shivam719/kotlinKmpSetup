package com.example.kotlinkmpsetup.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.kotlinkmpsetup.data.local.TokenDataStoreManager
import com.example.kotlinkmpsetup.domain.model.AppBanner
import com.example.kotlinkmpsetup.domain.model.ServiceItemModel
import com.example.kotlinkmpsetup.domain.model.User
import com.example.kotlinkmpsetup.presentation.home.HomeViewModel
import com.example.kotlinkmpsetup.ui.components.GlassButton
import com.example.kotlinkmpsetup.ui.components.GlassCard
import com.example.kotlinkmpsetup.ui.components.GlassDangerButton
import com.example.kotlinkmpsetup.ui.components.GlassSecondaryButton
import com.example.kotlinkmpsetup.ui.components.LiquidBackground
import com.example.kotlinkmpsetup.ui.components.glassShimmer
import com.example.kotlinkmpsetup.ui.theme.LocalGlassColors
import com.example.kotlinkmpsetup.ui.utils.AppLanguage
import com.example.kotlinkmpsetup.ui.utils.GlassLanguageToggle
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.milliseconds

/**
 * Bottom / Side Navigation Tabs for Home Dashboard
 */
enum class DashboardTab(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Default.Home),
    Profile("Profile", Icons.Default.Person),
    Report("Report", Icons.Default.Assessment),
    WhatsApp("WhatsApp", Icons.Default.Chat),
    Logout("Logout", Icons.AutoMirrored.Filled.Logout)
}

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: PRESENTATION (Home Dashboard Screen)
 * ====================================================================================
 * Main Retailer Dashboard with Adaptive Navigation (Bottom Bar for Mobile, Side Rail for Landscape/Tablet):
 * - Home (Balance, Quick Actions, Stats)
 * - Profile (User Info, Outlet Details, Role)
 * - Report (Transaction Reports & Activity)
 * - WhatsApp (Direct Support Chat Intent Launcher)
 * - Logout (Session purge & Return to Login)
 */
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToRecharge: () -> Unit = {},
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier,
    tokenManager: TokenDataStoreManager = koinInject()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTab by rememberSaveable { mutableStateOf(DashboardTab.Home) }
    val savedUser = remember { tokenManager.getSavedUser() }

    val handleTabSelected: (DashboardTab) -> Unit = { tab ->
        if (tab == DashboardTab.WhatsApp) {
            val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                data = "https://wa.me/919851937038?text=Hello%20AuraGlass%20Support".toUri()
            }
            try {
                context.startActivity(whatsappIntent)
            } catch (e: Exception) {
                // Fallback
            }
        } else {
            selectedTab = tab
        }
    }

    LiquidBackground(modifier = modifier) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .displayCutoutPadding()
                .imePadding()
        ) {
            val isWide = maxWidth >= 600.dp
            val isLandscape = maxWidth > maxHeight
            val isSideNav = isWide || isLandscape

            if (isSideNav) {
                // LANDSCAPE & TABLET LAYOUT: Side Navigation Rail + Dashboard Content
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Side Navigation Rail
                    GlassSideNavigationBar(
                        selectedTab = selectedTab,
                        onTabSelected = handleTabSelected,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 12.dp, horizontal = 12.dp)
                    )

                    // Right Main Dashboard Area
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        // Top Header Bar
                        TopDashboardHeader(
                            userName = savedUser?.fullName ?: "Rehan Telecom",
                            userId = savedUser?.id ?: "17"
                        )

                        // Tab Content View
                        AnimatedContent(
                            targetState = selectedTab,
                            transitionSpec = { fadeIn().togetherWith(fadeOut()) },
                            label = "TabTransition",
                            modifier = Modifier.weight(1f)
                        ) { tab ->
                            MainTabSwitcher(
                                tab = tab,
                                savedUser = savedUser,
                                onLogoutConfirmed = {
                                    scope.launch {
                                        tokenManager.clearTokens()
                                        onLogout()
                                    }
                                },
                                onCancelLogout = { selectedTab = DashboardTab.Home },
                                onNavigateToRecharge = onNavigateToRecharge,
                                currentLanguage = currentLanguage,
                                onLanguageChange = onLanguageChange
                            )
                        }
                    }
                }
            } else {
                // PORTRAIT MOBILE LAYOUT: Top Header + Main Content + Floating Bottom Bar
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp) // Space for floating bottom navigation bar
                    ) {
                        // Top Header Bar
                        TopDashboardHeader(
                            userName = savedUser?.fullName ?: "Rehan Telecom",
                            userId = savedUser?.id ?: "17"
                        )

                        // Tab Content View
                        AnimatedContent(
                            targetState = selectedTab,
                            transitionSpec = { fadeIn().togetherWith(fadeOut()) },
                            label = "TabTransition",
                            modifier = Modifier.weight(1f)
                        ) { tab ->
                            MainTabSwitcher(
                                tab = tab,
                                savedUser = savedUser,
                                onLogoutConfirmed = {
                                    scope.launch {
                                        tokenManager.clearTokens()
                                        onLogout()
                                    }
                                },
                                onCancelLogout = { selectedTab = DashboardTab.Home },
                                onNavigateToRecharge = onNavigateToRecharge,
                                currentLanguage = currentLanguage,
                                onLanguageChange = onLanguageChange
                            )
                        }
                    }

                    // Floating Glass Bottom Navigation Bar
                    GlassBottomNavigationBar(
                        selectedTab = selectedTab,
                        onTabSelected = handleTabSelected,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }
}

/**
 * Top Header Bar for Retailer Info
 */
@Composable
private fun TopDashboardHeader(
    userName: String,
    userId: String
) {
    val glassColors = LocalGlassColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = userName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = glassColors.textPrimary
            )
            Text(
                text = "Retailer ID: $userId",
                fontSize = 12.sp,
                color = glassColors.textSecondary
            )
        }
    }
}

/**
 * Switcher for Tab Content
 */
@Composable
private fun MainTabSwitcher(
    tab: DashboardTab,
    savedUser: User?,
    onLogoutConfirmed: () -> Unit,
    onCancelLogout: () -> Unit,
    onNavigateToRecharge: () -> Unit = {},
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit
) {
    when (tab) {
        DashboardTab.Home -> HomeTabContent(onNavigateToRecharge = onNavigateToRecharge)
        DashboardTab.Profile -> ProfileTabContent(savedUser, currentLanguage, onLanguageChange)
        DashboardTab.Report -> ReportTabContent()
        DashboardTab.WhatsApp -> HomeTabContent(onNavigateToRecharge = onNavigateToRecharge)
        DashboardTab.Logout -> LogoutTabContent(
            onConfirmLogout = onLogoutConfirmed,
            onCancel = onCancelLogout
        )
    }
}

/**
 * Home Tab View: Live Wallet Balance, Announcements & Quick Services with Pull-To-Refresh
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTabContent(
    onNavigateToRecharge: () -> Unit = {},
    homeViewModel: HomeViewModel = koinViewModel()
) {
    val glassColors = LocalGlassColors.current
    val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    var showBalance by rememberSaveable { mutableStateOf(true) }

    // Auto-Trigger Home Data API calls (GetBalance, GetAppBanner, GetAppNews, GetOpTypes) on Screen Arrival
    LaunchedEffect(Unit) {
        homeViewModel.loadHomeData()
    }

    PullToRefreshBox(
        isRefreshing = homeUiState.isRefreshing,
        onRefresh = { homeViewModel.refreshData() },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Wallet Balance Glass Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = 20.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Main Wallet Balance",
                            fontSize = 12.sp,
                            color = glassColors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (showBalance) homeUiState.balance.formattedMainBalance else "₹ ••••••••",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = glassColors.accentPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(glassColors.accentPrimary.copy(alpha = 0.15f))
                            .border(1.dp, glassColors.accentPrimary, CircleShape)
                            .clickable { showBalance = !showBalance },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wallet,
                            contentDescription = "Wallet",
                            tint = glassColors.accentPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Live News / Announcement Ticker Banner
            val content = homeUiState.news.content
                .takeIf { it.isNotBlank() }
                ?: homeUiState.news.newsDetail
            if (content.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = 14.dp
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "News",
                            tint = glassColors.accentSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = content,
                            fontSize = 12.sp,
                            color = glassColors.textPrimary,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Auto-Scrolling Glass Promo Banner Slider or Shimmer Skeleton
            if (homeUiState.isLoading && homeUiState.banners.isEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .glassShimmer(RoundedCornerShape(20.dp))
                )
            } else if (homeUiState.banners.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                BannerSlider(banners = homeUiState.banners)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Categorized Dynamic Services Grid from GetOpTypes API (ServiceSections)
            val allServices = homeUiState.services
            if (allServices.isNotEmpty()) {
                val bankingServices = allServices.filter { it.id in ServiceSections.BANKING }
                val rechargeServices = allServices.filter { it.id in ServiceSections.RECHARGE }
                val billServices = allServices.filter { it.id in ServiceSections.BILL }
                val utilityServices = allServices.filter { it.id in ServiceSections.UTILITY }
                val otherServices = allServices.filter {
                    it.id !in ServiceSections.BANKING &&
                    it.id !in ServiceSections.RECHARGE &&
                    it.id !in ServiceSections.BILL &&
                    it.id !in ServiceSections.UTILITY
                }

                if (bankingServices.isNotEmpty()) {
                    SectionCard(title = "Banking Services (24x7)") {
                        DynamicServiceRow(services = bankingServices, onItemClick = { onNavigateToRecharge() })
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                if (rechargeServices.isNotEmpty()) {
                    SectionCard(title = "Recharge Services") {
                        DynamicServiceRow(services = rechargeServices, onItemClick = { onNavigateToRecharge() })
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                if (billServices.isNotEmpty()) {
                    SectionCard(title = "Bill Payments") {
                        DynamicServiceRow(services = billServices, onItemClick = { onNavigateToRecharge() })
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                if (utilityServices.isNotEmpty()) {
                    SectionCard(title = "Utility Bills") {
                        DynamicServiceRow(services = utilityServices, onItemClick = { onNavigateToRecharge() })
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                if (otherServices.isNotEmpty()) {
                    SectionCard(title = "Other Services") {
                        DynamicServiceRow(services = otherServices, onItemClick = { onNavigateToRecharge() })
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }
            } else {
                SectionCard(title = "Quick Services") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickServiceItem(label = "Mobile", icon = Icons.Default.Phone)
                        QuickServiceItem(label = "DTH", icon = Icons.Default.ReceiptLong)
                        QuickServiceItem(label = "AEPS", icon = Icons.Default.Shield)
                        QuickServiceItem(label = "Reports", icon = Icons.Default.Assessment)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickServiceItem(label: String, icon: ImageVector) {
    val glassColors = LocalGlassColors.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(glassColors.glassSurface)
                .border(1.dp, glassColors.glassBorder, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = glassColors.accentPrimary,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = glassColors.textPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Service Sections Category IDs matching RMS / ArsenPay backend contracts
 */
private object ServiceSections {
    val BANKING = listOf(22, 60, 44, 14, 24, 15, 23, 452)
    val RECHARGE = listOf(1, 3, 5, 84)
    val BILL = listOf(2, 4, 16, 17, 26, 49, 123)
    val UTILITY = listOf(6, 25, 27, 28, 29, 35, 36, 38, 39, 46, 47, 48, 52, 54, 64, 69, 88, 90, 91, 92, 107, 112, 121, 122, 132, 133, 134)
}

/**
 * Reusable Glass Section Card Container
 */
@Composable
private fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val glassColors = LocalGlassColors.current

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 16.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = glassColors.textPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

/**
 * Dynamic Services Grid (Rows of 4 items for Mobile)
 */
@Composable
private fun DynamicServiceRow(
    services: List<ServiceItemModel>,
    modifier: Modifier = Modifier,
    onItemClick: (ServiceItemModel) -> Unit = {},
    onInactiveClick: (ServiceItemModel) -> Unit = {}
) {
    val chunkSize = 4
    val rows = services.chunked(chunkSize)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top
            ) {
                rowItems.forEach { item ->
                    ServiceGridItem(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onClick = { onItemClick(item) },
                        onInactiveClick = { onInactiveClick(item) }
                    )
                }
                repeat(chunkSize - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ServiceGridItem(
    item: ServiceItemModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onInactiveClick: () -> Unit = {}
) {
    val glassColors = LocalGlassColors.current

    val icon = when {
        item.name.contains("Mobile", ignoreCase = true) || item.name.contains("Prepaid", ignoreCase = true) || item.serviceType.contains("PREPAID", ignoreCase = true) -> Icons.Default.Phone
        item.name.contains("Postpaid", ignoreCase = true) -> Icons.Default.Phone
        item.name.contains("DTH", ignoreCase = true) || item.name.contains("BOX", ignoreCase = true) -> Icons.Default.ReceiptLong
        item.name.contains("AEPS", ignoreCase = true) || item.serviceType.contains("AEPS", ignoreCase = true) -> Icons.Default.Shield
        item.name.contains("Electricity", ignoreCase = true) -> Icons.Default.Bolt
        item.name.contains("Water", ignoreCase = true) -> Icons.Default.WaterDrop
        item.name.contains("Gas", ignoreCase = true) || item.name.contains("LPG", ignoreCase = true) -> Icons.Default.LocalGasStation
        item.name.contains("Money", ignoreCase = true) || item.name.contains("Transfer", ignoreCase = true) || item.name.contains("PSA", ignoreCase = true) -> Icons.Default.AccountBalance
        item.name.contains("Insurance", ignoreCase = true) -> Icons.Default.Shield
        item.name.contains("Report", ignoreCase = true) -> Icons.Default.Assessment
        else -> Icons.Default.AutoAwesome
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = item.isEnabled) { onClick() }
            .padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(glassColors.glassSurface)
                .border(
                    width = 1.dp,
                    color = if (item.isEnabled) glassColors.glassBorder else glassColors.glassBorder.copy(
                        alpha = 0.2f
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = item.name,
                tint = if (item.isEnabled) glassColors.accentPrimary else glassColors.textSecondary.copy(alpha = 0.4f),
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = item.name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (item.isEnabled) glassColors.textPrimary else glassColors.textSecondary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 14.sp
        )
    }
}

/**
 * Profile Tab View: Retailer Details & Language Switcher
 */
@Composable
private fun ProfileTabContent(
    user: User?,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit
) {
    val glassColors = LocalGlassColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = 24.dp
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(glassColors.glassSurface)
                        .border(1.5.dp, glassColors.accentPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = glassColors.accentPrimary,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = user?.fullName ?: "Rehan Telecom",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = glassColors.textPrimary
                )

                Text(
                    text = "Role: Retailer | ID: ${user?.id ?: "17"}",
                    fontSize = 13.sp,
                    color = glassColors.textSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = glassColors.glassBorder)
                Spacer(modifier = Modifier.height(16.dp))

                ProfileDetailRow("Mobile Number", user?.email ?: "9851937038")
                ProfileDetailRow("Outlet Name", "Rehan Telecom Center")
                ProfileDetailRow("State", "West Bengal")
                ProfileDetailRow("Pincode", "713101")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Exclusive App Language Switcher Card (English / हिंदी)
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = 20.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (currentLanguage == AppLanguage.ENGLISH) "App Language / भाषा चुनें" else "ऐप की भाषा / Choose Language",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = glassColors.textPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (currentLanguage == AppLanguage.ENGLISH) {
                        GlassButton(
                            text = "English",
                            onClick = { onLanguageChange(AppLanguage.ENGLISH) },
                            modifier = Modifier.weight(1f)
                        )
                        GlassSecondaryButton(
                            text = "हिंदी",
                            onClick = { onLanguageChange(AppLanguage.HINDI) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        GlassSecondaryButton(
                            text = "English",
                            onClick = { onLanguageChange(AppLanguage.ENGLISH) },
                            modifier = Modifier.weight(1f)
                        )
                        GlassButton(
                            text = "हिंदी",
                            onClick = { onLanguageChange(AppLanguage.HINDI) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileDetailRow(label: String, value: String) {
    val glassColors = LocalGlassColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = glassColors.textSecondary)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = glassColors.textPrimary)
    }
}

/**
 * Report Tab View: Transaction Activity
 */
@Composable
private fun ReportTabContent() {
    val glassColors = LocalGlassColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Recent Transactions",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = glassColors.textPrimary,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        TransactionReportCard("Mobile Recharge - Jio 299", "₹ 299.00", "SUCCESS", Color(0xFF10B981))
        TransactionReportCard("DTH Recharge - Tata Play", "₹ 450.00", "SUCCESS", Color(0xFF10B981))
        TransactionReportCard("AEPS Cash Withdrawal", "₹ 2,000.00", "SUCCESS", Color(0xFF10B981))
        TransactionReportCard("Electricity Bill Pay", "₹ 1,120.00", "PENDING", Color(0xFFF59E0B))
    }
}

@Composable
private fun TransactionReportCard(title: String, amount: String, status: String, statusColor: Color) {
    val glassColors = LocalGlassColors.current

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        contentPadding = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = glassColors.textPrimary)
                Text(text = status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = statusColor)
            }
            Text(text = amount, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = glassColors.accentPrimary)
        }
    }
}

/**
 * Logout Tab View: Confirmation Screen
 */
@Composable
private fun LogoutTabContent(onConfirmLogout: () -> Unit, onCancel: () -> Unit) {
    val glassColors = LocalGlassColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 400.dp),
            contentPadding = 24.dp
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = glassColors.accentTertiary,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Confirm Logout",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = glassColors.textPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Are you sure you want to log out of your session?",
                    fontSize = 14.sp,
                    color = glassColors.textSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    GlassSecondaryButton(
                        text = "Cancel",
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    GlassDangerButton(
                        text = "Log Out",
                        onClick = onConfirmLogout,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Reusable Glass Bottom Navigation Bar for Portrait Mobile Layout
 */
@Composable
private fun GlassBottomNavigationBar(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(glassColors.glassSurface)
            .border(1.2.dp, glassColors.glassBorder, RoundedCornerShape(24.dp))
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DashboardTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                val tint = if (isSelected) glassColors.accentPrimary else glassColors.textSecondary

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = tint,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tab.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = tint
                    )
                }
            }
        }
    }
}

/**
 * Reusable Glass Side Navigation Rail (Jetpack Compose Material 3 NavigationRail)
 * for Landscape & Tablet Layouts
 */
@Composable
private fun GlassSideNavigationBar(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current

    NavigationRail(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .border(1.2.dp, glassColors.glassBorder, RoundedCornerShape(24.dp)),
        containerColor = glassColors.glassSurface,
        contentColor = glassColors.textPrimary,
        header = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)
            ) {
                // Prominent App Logo Sphere
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(glassColors.accentPrimary.copy(alpha = 0.15f))
                        .border(1.5.dp, glassColors.accentPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "App Logo",
                        tint = glassColors.accentPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "AuraGlass",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = glassColors.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DashboardTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab

                NavigationRailItem(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            text = tab.label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = glassColors.accentPrimary,
                        selectedTextColor = glassColors.accentPrimary,
                        indicatorColor = glassColors.accentPrimary.copy(alpha = 0.18f),
                        unselectedIconColor = glassColors.textSecondary,
                        unselectedTextColor = glassColors.textSecondary
                    )
                )
            }
        }
    }
}

/**
 * Auto-Scrolling Glass Promo Banner Slider with Glass Dot Indicators
 */
@Composable
private fun BannerSlider(
    banners: List<AppBanner>,
    modifier: Modifier = Modifier
) {
    if (banners.isEmpty()) return

    val context = LocalContext.current
    val glassColors = LocalGlassColors.current
    val pagerState = rememberPagerState(initialPage = 1000, pageCount = { Int.MAX_VALUE })

    LaunchedEffect(banners) {
        while (isActive) {
            delay(3500.milliseconds)
            val nextPage = pagerState.currentPage + 1
            pagerState.animateScrollToPage(
                page = nextPage,
                animationSpec = tween(durationMillis = 600, easing = LinearEasing)
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(glassColors.glassSurface)
            .border(1.2.dp, glassColors.glassBorder, RoundedCornerShape(20.dp))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val index = page % banners.size
            val banner = banners[index]

            SubcomposeAsyncImage(
                model = banner.imageUrl,
                contentDescription = "Promo Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = banner.redirectUrl.isNotBlank()) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, banner.redirectUrl.toUri())
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Fallback
                        }
                    },
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .glassShimmer(RoundedCornerShape(20.dp))
                    )
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .glassShimmer(RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Banner Error",
                            tint = glassColors.textSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            )
        }

        // Indicator Glass Dots
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 14.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val currentIndex = pagerState.currentPage % banners.size
            repeat(banners.size) { index ->
                val isSelected = currentIndex == index
                val dotWidth = if (isSelected) 18.dp else 8.dp
                val dotColor = if (isSelected) glassColors.accentPrimary else glassColors.textSecondary.copy(alpha = 0.4f)

                Box(
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .height(6.dp)
                        .width(dotWidth)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            }
        }
    }
}
