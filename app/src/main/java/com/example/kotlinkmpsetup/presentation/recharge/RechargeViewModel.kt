package com.example.kotlinkmpsetup.presentation.recharge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinkmpsetup.data.remote.dto.BillReqDto
import com.example.kotlinkmpsetup.domain.model.BillFetchResult
import com.example.kotlinkmpsetup.domain.model.CircleModel
import com.example.kotlinkmpsetup.domain.model.DthCustomerInfo
import com.example.kotlinkmpsetup.domain.model.NetworkResult
import com.example.kotlinkmpsetup.domain.model.OfferPlanModel
import com.example.kotlinkmpsetup.domain.model.OpOptionalOptionModel
import com.example.kotlinkmpsetup.domain.model.OperatorModel
import com.example.kotlinkmpsetup.domain.model.OperatorParamModel
import com.example.kotlinkmpsetup.domain.model.PlanTypeModel
import com.example.kotlinkmpsetup.domain.model.TransactionResultModel
import com.example.kotlinkmpsetup.domain.repository.RechargeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: PRESENTATION (Recharge UI State)
 * ====================================================================================
 */
data class RechargeUiState(
    val selectedTab: Int = 0, // 0: Prepaid, 1: Postpaid, 2: DTH
    val operators: List<OperatorModel> = emptyList(),
    val selectedOperator: OperatorModel? = null,
    val circles: List<CircleModel> = emptyList(),
    val selectedCircle: CircleModel? = null,
    val showCircleBottomSheet: Boolean = false,
    val optionalParams: List<OperatorParamModel> = emptyList(),
    val dropdownOptions: List<OpOptionalOptionModel> = emptyList(),
    val plansList: List<PlanTypeModel> = emptyList(),
    val offerPlansList: List<OfferPlanModel> = emptyList(),
    val selectedPlanTab: Int = 0, // 0: Simple Plans, 1: R-Offers
    val optionText1: String = "",
    val optionText2: String = "",
    val optionText3: String = "",
    val optionText4: String = "",
    val accountNumber: String = "",
    val customerNumber: String = "",
    val amount: String = "",
    val securityPin: String = "",
    val isLoading: Boolean = false,
    val isFetchingBill: Boolean = false,
    val billFetchResult: BillFetchResult? = null,
    val isTransactionSuccess: Boolean = false,
    val transactionResult: TransactionResultModel? = null,
    val dthCustomerInfoList: List<DthCustomerInfo> = emptyList(),
    val errorMessage: String? = null
)

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: PRESENTATION (Recharge ViewModel)
 * ====================================================================================
 */
class RechargeViewModel(
    private val rechargeRepository: RechargeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RechargeUiState())
    val uiState: StateFlow<RechargeUiState> = _uiState.asStateFlow()

    init {
        loadCircles()
        loadOperatorsForCurrentTab(0)
    }

    private fun loadCircles() {
        viewModelScope.launch {
            when (val result = rechargeRepository.getCircles()) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(circles = result.data)
                    }
                }
                else -> {}
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update {
            it.copy(
                selectedTab = index,
                accountNumber = "",
                customerNumber = "",
                amount = "",
                selectedOperator = null,
                selectedCircle = null,
                showCircleBottomSheet = false,
                optionalParams = emptyList(),
                dropdownOptions = emptyList(),
                plansList = emptyList(),
                offerPlansList = emptyList(),
                optionText1 = "",
                optionText2 = "",
                optionText3 = "",
                optionText4 = "",
                billFetchResult = null,
                errorMessage = null
            )
        }
        loadOperatorsForCurrentTab(index)
    }

    fun loadOperatorsForCurrentTab(tabIndex: Int) {
        val serviceId = when (tabIndex) {
            0 -> 1 // Prepaid
            1 -> 2 // Postpaid
            else -> 3 // DTH
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = rechargeRepository.getOperators(serviceId)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            operators = result.data,
                            selectedOperator = null // NO default operator selected
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun selectOperator(operator: OperatorModel) {
        _uiState.update {
            it.copy(
                selectedOperator = operator,
                showCircleBottomSheet = it.selectedTab == 0, // Auto open Circle BottomSheet on Prepaid operator select
                optionalParams = emptyList(),
                dropdownOptions = emptyList(),
                optionText1 = "",
                optionText2 = "",
                optionText3 = "",
                optionText4 = ""
            )
        }
        loadOperatorOptionals(operator.oid)
    }

    fun selectCircle(circle: CircleModel) {
        _uiState.update {
            it.copy(
                selectedCircle = circle,
                showCircleBottomSheet = false
            )
        }
    }

    fun toggleCircleBottomSheet(show: Boolean) {
        _uiState.update { it.copy(showCircleBottomSheet = show) }
    }

    fun setSelectedPlanTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedPlanTab = tabIndex) }
    }

    fun onViewPlansClicked() {
        val state = _uiState.value
        if (state.selectedOperator == null) {
            _uiState.update { it.copy(errorMessage = "Please select operator") }
            return
        }
        if (state.selectedCircle == null) {
            _uiState.update {
                it.copy(
                    showCircleBottomSheet = true,
                    errorMessage = "Please select Circle"
                )
            }
            return
        }

        loadRechargePlans()
    }

    fun onRNPOfferClicked() {
        val state = _uiState.value
        if (state.selectedOperator == null) {
            _uiState.update { it.copy(errorMessage = "Please select operator") }
            return
        }
        if (state.selectedCircle == null) {
            _uiState.update {
                it.copy(
                    showCircleBottomSheet = true,
                    errorMessage = "Please select Circle"
                )
            }
            return
        }
        if (state.accountNumber.length < 10) {
            _uiState.update { it.copy(errorMessage = "Please enter valid 10-digit mobile number") }
            return
        }

        loadBestOffers()
    }

    fun loadRechargePlans() {
        val state = _uiState.value
        val circleId = state.selectedCircle?.id ?: 0
        val oid = state.selectedOperator?.oid ?: 0
        if (oid == 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = if (state.selectedTab == 2) {
                rechargeRepository.getDTHSimplePlan(oid, state.accountNumber)
            } else {
                rechargeRepository.getRechargePlans(circleId, oid)
            }

            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            plansList = result.data,
                            selectedPlanTab = 0
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun loadDthPlansByLanguage(language: String) {
        val state = _uiState.value
        val oid = state.selectedOperator?.oid ?: 0
        if (oid == 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = if (language.equals("All", ignoreCase = true)) {
                rechargeRepository.getDTHSimplePlan(oid, state.accountNumber)
            } else {
                rechargeRepository.getDthPlanByLang(oid, language, state.accountNumber)
            }

            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            plansList = result.data
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun loadBestOffers() {
        val state = _uiState.value
        val mobile = state.accountNumber.trim()
        val oid = state.selectedOperator?.oid ?: 0
        if (mobile.length < 10 || oid == 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = rechargeRepository.getRNPRoffer(mobile, oid)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            offerPlansList = result.data,
                            selectedPlanTab = 1
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun loadOperatorOptionals(oid: Int) {
        viewModelScope.launch {
            when (val result = rechargeRepository.getOperatorOptionals(oid)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            optionalParams = result.data.operatorParams,
                            dropdownOptions = result.data.dropdownOptions
                        )
                    }
                }
                else -> {}
            }
        }
    }

    fun onAccountNumberChanged(value: String) {
        _uiState.update { it.copy(accountNumber = value) }
        // Trigger HLR Lookup automatically if 10-digit mobile number is entered on Prepaid tab
        if (_uiState.value.selectedTab == 0 && value.length == 10 && value.all { it.isDigit() }) {
            performHlrLookUp(value)
        }
    }

    private fun performHlrLookUp(mobileNumber: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = rechargeRepository.getHlrLookUp(mobileNumber)) {
                is NetworkResult.Success -> {
                    val matchingOp = _uiState.value.operators.firstOrNull { it.oid == result.data.oid }
                    val matchingCircle = _uiState.value.circles.firstOrNull { it.id == result.data.circleId }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            selectedOperator = matchingOp ?: it.selectedOperator,
                            selectedCircle = matchingCircle ?: it.selectedCircle
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun triggerHeavyRefresh() {
        val state = _uiState.value
        val operator = state.selectedOperator ?: return
        if (state.accountNumber.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter valid DTH Number") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = rechargeRepository.getDthHeavyRefresh(state.accountNumber.trim(), operator.oid)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.data
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onCustomerNumberChanged(value: String) {
        _uiState.update { it.copy(customerNumber = value) }
    }

    fun onAmountChanged(value: String) {
        _uiState.update { it.copy(amount = value) }
    }

    fun onOptionTextChanged(index: Int, value: String) {
        _uiState.update {
            when (index) {
                0 -> it.copy(optionText1 = value)
                1 -> it.copy(optionText2 = value)
                2 -> it.copy(optionText3 = value)
                else -> it.copy(optionText4 = value)
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearBillFetchResult() {
        _uiState.update { it.copy(billFetchResult = null) }
    }

    fun clearTransactionResult() {
        _uiState.update { it.copy(isTransactionSuccess = false, transactionResult = null) }
    }

    fun fetchBill() {
        val state = _uiState.value
        val operator = state.selectedOperator ?: return
        if (state.accountNumber.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter valid ${operator.accountName}") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isFetchingBill = true, errorMessage = null) }
            val req = BillReqDto(
                accountNo = state.accountNumber.trim(),
                amount = state.amount.ifBlank { "0" },
                customerNo = state.customerNumber.trim(),
                geoCode = "26.8539515,81.025492",
                o1 = state.optionText1.trim(),
                o2 = state.optionText2.trim(),
                o3 = state.optionText3.trim(),
                o4 = state.optionText4.trim(),
                oid = operator.oid.toString()
            )
            when (val result = rechargeRepository.fetchBill(req)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isFetchingBill = false,
                            billFetchResult = result.data,
                            amount = result.data.amount.toString()
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isFetchingBill = false,
                            errorMessage = result.message
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isFetchingBill = false) }
                }
            }
        }
    }

    fun submitTransaction(pin: String) {
        val state = _uiState.value
        val operator = state.selectedOperator ?: return
        if (state.accountNumber.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter valid ${operator.accountName}") }
            return
        }
        if (state.amount.isBlank() || (state.amount.toDoubleOrNull() ?: 0.0) <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid amount") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val req = BillReqDto(
                accountNo = state.accountNumber.trim(),
                amount = state.amount.trim(),
                customerNo = state.customerNumber.trim(),
                geoCode = "26.8539515,81.025492",
                o1 = state.optionText1.trim(),
                o2 = state.optionText2.trim(),
                o3 = state.optionText3.trim(),
                o4 = state.optionText4.trim(),
                oid = operator.oid.toString(),
                fetchBillID = state.billFetchResult?.fetchBillId ?: 0,
                refID = state.billFetchResult?.referenceId.orEmpty(),
                securityKey = pin
            )
            when (val result = rechargeRepository.doTransaction(req)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isTransactionSuccess = true,
                            transactionResult = result.data
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun fetchDthInfo() {
        val state = _uiState.value
        val operator = state.selectedOperator ?: return
        if (state.accountNumber.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter valid DTH Number") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = rechargeRepository.getDthCustomerInfo(state.accountNumber.trim(), operator.oid)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            dthCustomerInfoList = result.data
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }
}
