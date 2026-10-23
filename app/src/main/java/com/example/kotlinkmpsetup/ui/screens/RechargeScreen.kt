package com.example.kotlinkmpsetup.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.kotlinkmpsetup.data.remote.ApiEndpoints
import com.example.kotlinkmpsetup.domain.model.OperatorModel
import com.example.kotlinkmpsetup.presentation.recharge.RechargeViewModel
import com.example.kotlinkmpsetup.ui.components.DialogStatusType
import com.example.kotlinkmpsetup.ui.components.GlassButton
import com.example.kotlinkmpsetup.ui.components.GlassCard
import com.example.kotlinkmpsetup.ui.components.GlassIconButton
import com.example.kotlinkmpsetup.ui.components.GlassSnackbar
import com.example.kotlinkmpsetup.ui.components.GlassStatusDialog
import com.example.kotlinkmpsetup.ui.components.GlassTextField
import com.example.kotlinkmpsetup.ui.components.LineLoading
import com.example.kotlinkmpsetup.ui.components.LiquidBackground
import com.example.kotlinkmpsetup.ui.components.SnackbarType
import com.example.kotlinkmpsetup.ui.components.rememberGlassToastState
import com.example.kotlinkmpsetup.ui.theme.LocalGlassColors
import org.koin.androidx.compose.koinViewModel

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: PRESENTATION (Recharge & Bill Payment Screen View)
 * ====================================================================================
 * Complete UI for Mobile Prepaid, Postpaid, and DTH Recharges with Operator Select,
 * BBPS Bill Fetching, Confirmation Dialog with PIN, and Receipt Overlay.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RechargeScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    initialTab: Int = 0,
    viewModel: RechargeViewModel = koinViewModel()
) {
    val glassColors = LocalGlassColors.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toastController = rememberGlassToastState()

    var showOperatorDropdown by rememberSaveable { mutableStateOf(false) }
    var showConfirmPinDialog by rememberSaveable { mutableStateOf(false) }
    var pinText by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(initialTab) {
        viewModel.selectTab(initialTab)
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            toastController.show(msg, SnackbarType.Error)
            viewModel.clearError()
        }
    }

    LiquidBackground(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassIconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(38.dp),
                        iconContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = glassColors.textPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Recharge & Bill Pay",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.textPrimary
                    )
                }

                // Sticky Tab Bar (Prepaid, Postpaid, DTH)
                val tabs = listOf("Prepaid", "Postpaid", "DTH")
                TabRow(
                    selectedTabIndex = uiState.selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = glassColors.accentPrimary,
                    indicator = { tabPositions ->
                        if (uiState.selectedTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                                height = 3.dp,
                                color = glassColors.accentPrimary
                            )
                        }
                    },
                    divider = { HorizontalDivider(color = glassColors.glassBorder) }
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = uiState.selectedTab == index
                        Tab(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(index) },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) glassColors.accentPrimary else glassColors.textSecondary
                                )
                            }
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    LineLoading(isLoading = uiState.isLoading || uiState.isFetchingBill)

                    // Operator Selection Glass Card
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showOperatorDropdown = true },
                        contentPadding = 16.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!uiState.selectedOperator?.image.isNullOrBlank()) {
                                    AsyncImage(
                                        model = "${ApiEndpoints.Services.IMAGE_URL}${uiState.selectedOperator?.image}",
                                        contentDescription = "Operator Logo",
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .border(1.dp, glassColors.glassBorder, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(glassColors.accentPrimary.copy(alpha = 0.15f))
                                            .border(1.dp, glassColors.accentPrimary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = "Operator",
                                            tint = glassColors.accentPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                }

                                Column {
                                    Text(
                                        text = uiState.selectedOperator?.name ?: "Select Operator",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.selectedOperator != null) glassColors.textPrimary else glassColors.accentPrimary
                                    )
                                    Text(
                                        text = "Tap to choose operator",
                                        fontSize = 11.sp,
                                        color = glassColors.textSecondary
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = glassColors.textPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Circle Selection Glass Card (For Prepaid)
                    if (uiState.selectedTab == 0) {
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleCircleBottomSheet(true) },
                            contentPadding = 14.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = uiState.selectedCircle?.name ?: "Select Circle",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.selectedCircle != null) glassColors.textPrimary else glassColors.accentPrimary
                                    )
                                    Text(
                                        text = "Telecom Circle",
                                        fontSize = 11.sp,
                                        color = glassColors.textSecondary
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Circle Dropdown",
                                    tint = glassColors.textPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // View Plans & R-Offer Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            GlassButton(
                                text = "View Plans",
                                onClick = { viewModel.onViewPlansClicked() },
                                modifier = Modifier.weight(1f)
                            )
                            GlassButton(
                                text = "R-Offer",
                                onClick = { viewModel.onRNPOfferClicked() },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Account / Mobile Number Input
                    val accountLabel = uiState.selectedOperator?.accountName ?: "Mobile Number"
                    GlassTextField(
                        value = uiState.accountNumber,
                        onValueChange = { viewModel.onAccountNumberChanged(it) },
                        label = accountLabel,
                        placeholder = "Enter $accountLabel",
                        leadingIcon = if (uiState.selectedTab == 2) Icons.Default.ReceiptLong else Icons.Default.Phone,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        )
                    )

                    // Customer Number (if required by operator)
                    if (uiState.selectedOperator?.isTakeCustomerNum == true) {
                        Spacer(modifier = Modifier.height(14.dp))
                        GlassTextField(
                            value = uiState.customerNumber,
                            onValueChange = { viewModel.onCustomerNumberChanged(it) },
                            label = "Customer Number",
                            placeholder = "Enter 10 digit Mobile Number",
                            leadingIcon = Icons.Default.Phone,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next
                            )
                        )
                    }

                    // Dynamic Optional Operator Params (Sub-Division / Account ID / Circle)
                    uiState.optionalParams.take(4).forEachIndexed { index, param ->
                        Spacer(modifier = Modifier.height(14.dp))
                        val currentVal = when (index) {
                            0 -> uiState.optionText1
                            1 -> uiState.optionText2
                            2 -> uiState.optionText3
                            else -> uiState.optionText4
                        }

                        GlassTextField(
                            value = currentVal,
                            onValueChange = { viewModel.onOptionTextChanged(index, it) },
                            label = param.paramName,
                            placeholder = "Enter ${param.paramName}",
                            leadingIcon = Icons.Default.Shield,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = if (param.dataType == "NUMERIC") KeyboardType.Number else KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Amount Input
                    GlassTextField(
                        value = uiState.amount,
                        onValueChange = { viewModel.onAmountChanged(it) },
                        label = "Recharge Amount (₹)",
                        placeholder = "Enter Amount (e.g. 299)",
                        leadingIcon = Icons.Default.Wallet,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons Row
                    val isBillingOperator = uiState.selectedOperator?.isBilling == true
                    if (isBillingOperator) {
                        GlassButton(
                            text = "Fetch Bill",
                            onClick = { viewModel.fetchBill() },
                            isLoading = uiState.isFetchingBill
                        )
                    } else if (uiState.selectedTab == 2) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            GlassButton(
                                text = "DTH Info",
                                onClick = { viewModel.fetchDthInfo() },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            GlassButton(
                                text = "Heavy Refresh",
                                onClick = { viewModel.triggerHeavyRefresh() },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            GlassButton(
                                text = "Recharge",
                                onClick = { showConfirmPinDialog = true },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        GlassButton(
                            text = "Proceed to Recharge",
                            onClick = { showConfirmPinDialog = true },
                            isLoading = uiState.isLoading
                        )
                    }

                    // Display Bill Details Card if bill fetched successfully
                    if (uiState.billFetchResult != null) {
                        Spacer(modifier = Modifier.height(18.dp))
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = 20.dp
                        ) {
                            Column {
                                Text(
                                    text = "Bill Details",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = glassColors.textPrimary
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Customer Name", fontSize = 13.sp, color = glassColors.textSecondary)
                                    Text(
                                        text = uiState.billFetchResult?.customerName ?: "N/A",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = glassColors.textPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Bill Amount", fontSize = 13.sp, color = glassColors.textSecondary)
                                    Text(
                                        text = "₹ ${uiState.billFetchResult?.amount ?: 0.0}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = glassColors.accentPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                GlassButton(
                                    text = "Pay Bill Now",
                                    onClick = { showConfirmPinDialog = true }
                                )
                            }
                        }
                    }

                    // Animated Plan & Offer Section with Tabs
                    if (uiState.plansList.isNotEmpty() || uiState.offerPlansList.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(20.dp))

                        val planTabs = listOf("Simple Plans", "R-Offers")
                        SecondaryTabRow(
                            selectedTabIndex = uiState.selectedPlanTab,
                            containerColor = Color.Transparent,
                            contentColor = glassColors.accentPrimary
                        ) {
                            planTabs.forEachIndexed { idx, title ->
                                Tab(
                                    selected = uiState.selectedPlanTab == idx,
                                    onClick = { viewModel.setSelectedPlanTab(idx) },
                                    text = {
                                        Text(
                                            text = title,
                                            fontSize = 13.sp,
                                            fontWeight = if (uiState.selectedPlanTab == idx) FontWeight.Bold else FontWeight.Medium,
                                            color = if (uiState.selectedPlanTab == idx) glassColors.accentPrimary else glassColors.textSecondary
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        AnimatedContent(
                            targetState = uiState.selectedPlanTab,
                            transitionSpec = { fadeIn().togetherWith(fadeOut()) },
                            label = "PlanTabTransition"
                        ) { planTab ->
                            if (planTab == 0) {
                                Column {
                                    if (uiState.plansList.isEmpty()) {
                                        Text("No simple plans loaded. Tap 'View Plans' above.", color = glassColors.textSecondary, fontSize = 13.sp)
                                    } else {
                                        uiState.plansList.forEach { planType ->
                                            GlassCard(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 6.dp),
                                                contentPadding = 14.dp
                                            ) {
                                                Column {
                                                    Text(
                                                        text = planType.planType,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = glassColors.accentPrimary
                                                    )
                                                    Spacer(modifier = Modifier.height(8.dp))

                                                    planType.details.take(5).forEach { detail ->
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clip(RoundedCornerShape(8.dp))
                                                                .clickable { viewModel.onAmountChanged(detail.price) }
                                                                .padding(vertical = 6.dp, horizontal = 8.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                    text = detail.desc,
                                                                    fontSize = 12.sp,
                                                                    color = glassColors.textPrimary,
                                                                    maxLines = 2
                                                                )
                                                                if (detail.validity.isNotBlank()) {
                                                                    Text(
                                                                        text = "Validity: ${detail.validity}",
                                                                        fontSize = 11.sp,
                                                                        color = glassColors.textSecondary
                                                                    )
                                                                }
                                                            }
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            Box(
                                                                modifier = Modifier
                                                                    .clip(RoundedCornerShape(10.dp))
                                                                    .background(glassColors.accentPrimary.copy(alpha = 0.2f))
                                                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                                            ) {
                                                                Text(
                                                                    text = "₹${detail.price}",
                                                                    fontSize = 13.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = glassColors.accentPrimary
                                                                )
                                                            }
                                                        }
                                                        HorizontalDivider(color = glassColors.glassBorder.copy(alpha = 0.3f))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Column {
                                    if (uiState.offerPlansList.isEmpty()) {
                                        Text("Tap 'R-Offer' button above to load personalized best offers.", color = glassColors.textSecondary, fontSize = 13.sp)
                                    } else {
                                        uiState.offerPlansList.forEach { offer ->
                                            GlassCard(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp)
                                                    .clickable { viewModel.onAmountChanged(offer.amount) },
                                                contentPadding = 12.dp
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = offer.description,
                                                        fontSize = 12.sp,
                                                        color = glassColors.textPrimary,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(10.dp))
                                                            .background(glassColors.accentPrimary.copy(alpha = 0.2f))
                                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                                    ) {
                                                        Text(
                                                            text = "₹${offer.amount}",
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = glassColors.accentPrimary
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Operator Selection Bottom Sheet Modal
            if (showOperatorDropdown) {
                ModalBottomSheet(
                    onDismissRequest = { showOperatorDropdown = false }
                ) {
                    var searchOperatorText by remember { mutableStateOf("") }
                    val filteredOperators = remember(searchOperatorText, uiState.operators) {
                        uiState.operators.filter {
                            it.name.contains(searchOperatorText.trim(), ignoreCase = true)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Select Provider / Operator",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        GlassTextField(
                            value = searchOperatorText,
                            onValueChange = { searchOperatorText = it },
                            label = "Search Operator",
                            placeholder = "Search (e.g. Jio, Airtel, Vi, BSNL)...",
                            leadingIcon = Icons.Default.Search
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                        ) {
                            items(filteredOperators.size) { index ->
                                val op = filteredOperators[index]
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            viewModel.selectOperator(op)
                                            showOperatorDropdown = false
                                        }
                                        .padding(vertical = 10.dp, horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (op.image.isNotBlank()) {
                                        AsyncImage(
                                            model = "${ApiEndpoints.Services.IMAGE_URL}${op.image}",
                                            contentDescription = op.name,
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .border(1.dp, glassColors.glassBorder, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(14.dp))
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(glassColors.accentPrimary.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Phone,
                                                contentDescription = op.name,
                                                tint = glassColors.accentPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(14.dp))
                                    }

                                    Text(
                                        text = op.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = glassColors.textPrimary
                                    )
                                }
                                HorizontalDivider(color = glassColors.glassBorder.copy(alpha = 0.3f))
                            }
                        }
                    }
                }
            }

            // Circle Selection Bottom Sheet Modal
            if (uiState.showCircleBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.toggleCircleBottomSheet(false) }
                ) {
                    var searchCircleText by remember { mutableStateOf("") }
                    val filteredCircles = remember(searchCircleText, uiState.circles) {
                        uiState.circles.filter {
                            it.name.contains(searchCircleText.trim(), ignoreCase = true)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Select Telecom Circle",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        GlassTextField(
                            value = searchCircleText,
                            onValueChange = { searchCircleText = it },
                            label = "Search Circle",
                            placeholder = "Search circle (e.g. Delhi, West Bengal)...",
                            leadingIcon = Icons.Default.Search
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        ) {
                            items(filteredCircles.size) { index ->
                                val circle = filteredCircles[index]
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { viewModel.selectCircle(circle) }
                                        .padding(vertical = 12.dp, horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = circle.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = glassColors.textPrimary
                                    )
                                }
                                HorizontalDivider(color = glassColors.glassBorder.copy(alpha = 0.3f))
                            }
                        }
                    }
                }
            }

            // PIN Confirmation Dialog Overlay
            if (showConfirmPinDialog) {
                GlassStatusDialog(
                    statusType = DialogStatusType.Info,
                    title = "Confirm Transaction",
                    message = "Recharge ${uiState.accountNumber} with ₹ ${uiState.amount} via ${uiState.selectedOperator?.name ?: "Operator"}",
                    confirmText = "Submit",
                    onConfirm = {
                        showConfirmPinDialog = false
                        viewModel.submitTransaction(pinText)
                    },
                    dismissText = "Cancel",
                    onDismiss = { showConfirmPinDialog = false }
                )
            }

            // Transaction Result Success Dialog
            if (uiState.isTransactionSuccess) {
                GlassStatusDialog(
                    statusType = DialogStatusType.Success,
                    title = "Transaction Successful!",
                    message = "${uiState.transactionResult?.message}\nTransaction ID: ${uiState.transactionResult?.transactionId}",
                    confirmText = "OK",
                    onDismiss = {
                        viewModel.clearTransactionResult()
                        onNavigateBack()
                    }
                )
            }

            // Glass Toast Notifications
            GlassSnackbar(
                message = toastController.state().message,
                visible = toastController.state().isVisible,
                type = toastController.state().type,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
