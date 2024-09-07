package com.judahben149.fourthwall.presentation.home

import androidx.lifecycle.ViewModel
import com.judahben149.fourthwall.data.local.relations.UserWithCurrencyAccounts
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.domain.usecase.orders.GetUserWithCurrencyAccountsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getUserAccountUseCase: GetUserWithCurrencyAccountsUseCase,
    private val sessionManager: SessionManager
): ViewModel() {

    private val _state = MutableStateFlow(HomeState(getUserAccountUseCase(sessionManager.getUserId())))
    val state: StateFlow<HomeState> = _state


}

data class HomeState(
    val userAccount: Flow<UserWithCurrencyAccounts?>
)