package com.judahben149.fourthwall.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.domain.usecase.orders.GetAllOrdersUseCase
import com.judahben149.fourthwall.domain.usecase.user.GetUserWithCurrencyAccountsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getUserAccountUseCase: GetUserWithCurrencyAccountsUseCase,
    getAllOrdersUseCase: GetAllOrdersUseCase,
    private val sessionManager: SessionManager
): ViewModel() {

    private val _userAccount = getUserAccountUseCase(sessionManager.getUserId())
    val userAccount = _userAccount.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    private val _allOrders = getAllOrdersUseCase()
    val allOrders = _allOrders.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )


}