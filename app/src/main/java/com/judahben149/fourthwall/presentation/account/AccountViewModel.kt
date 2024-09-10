package com.judahben149.fourthwall.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.fourthwall.domain.mappers.toCurrencyAccount
import com.judahben149.fourthwall.domain.usecase.user.GetCurrencyAccountUseCase
import com.judahben149.fourthwall.domain.usecase.user.TopUpBalanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val topUpBalanceUseCase: TopUpBalanceUseCase,
    private val getCurrencyAccountUseCase: GetCurrencyAccountUseCase
): ViewModel() {


    fun fundAccount(accountId: Int, amount: Double) {

        viewModelScope.launch(Dispatchers.IO) {
            topUpBalanceUseCase(accountId, amount)
        }
    }

    fun getCurrencyCode(accountId: Int, callback: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val currencyAccountEntity = getCurrencyAccountUseCase(accountId)

            currencyAccountEntity?.toCurrencyAccount()?.let {
                val currencyCode = it.currencyCode

                withContext(Dispatchers.Main) {
                    callback(currencyCode)
                }
            }
        }
    }
}