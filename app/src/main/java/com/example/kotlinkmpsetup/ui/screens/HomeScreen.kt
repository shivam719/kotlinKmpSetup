package com.example.kotlinkmpsetup.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wallet
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kotlinkmpsetup.data.local.TokenDataStoreManager
import com.example.kotlinkmpsetup.domain.model.User
import com.example.kotlinkmpsetup.presentation.home.HomeViewModel
import com.example.kotlinkmpsetup.ui.components.GlassCard
import com.example.kotlinkmpsetup.ui.components.GlassDangerButton
import com.example.kotlinkmpsetup.ui.components.GlassSecondaryButton
import com.example.kotlinkmpsetup.ui.components.LiquidBackground
import com.example.kotlinkmpsetup.ui.theme.LocalGlassColors
import com.example.kotlinkmpsetup.ui.utils.AppLanguage
import com.example.kotlinkmpsetup.ui.utils.GlassLanguageToggle
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

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
                            userId = savedUser?.id ?: "17",
                            currentLanguage = currentLanguage,
                            onLanguageChange = onLanguageChange
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
                                onCancelLogout = { selectedTab = DashboardTab.Home }
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
                            userId = savedUser?.id ?: "17",
                            currentLanguage = currentLanguage,
                            onLanguageChange = onLanguageChange
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
                                onCancelLogout = { selectedTab = DashboardTab.Home }
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
 * Top Header Bar for Retailer Info & Language Switcher
 */
@Composable
private fun TopDashboardHeader(
    userName: String,
    userId: String,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit
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

        GlassLanguageToggle(
            currentLanguage = currentLanguage,
            onLanguageChange = onLanguageChange
        )
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
    onCancelLogout: () -> Unit
) {
    when (tab) {
        DashboardTab.Home -> HomeTabContent()
        DashboardTab.Profile -> ProfileTabContent(savedUser)
        DashboardTab.Report -> ReportTabContent()
        DashboardTab.WhatsApp -> HomeTabContent()
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
            if (homeUiState.news.content.isNotBlank()) {
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
                            text = homeUiState.news.content,
                            fontSize = 12.sp,
                            color = glassColors.textPrimary,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Services Grid
            Text(
                text = "Quick Services",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = glassColors.textPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

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
 * Profile Tab View: Retailer Details
 */
@Composable
private fun ProfileTabContent(user: User?) {
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
