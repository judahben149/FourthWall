package com.judahben149.fourthwall.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.fourthwall.data.local.entities.CurrencyAccountEntity
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.domain.mappers.toCurrencyAccount
import com.judahben149.fourthwall.domain.usecase.user.GetCurrencyAccountUseCase
import com.judahben149.fourthwall.domain.usecase.user.InsertCurrencyAccountUseCase
import com.judahben149.fourthwall.domain.usecase.user.TopUpBalanceUseCase
import com.judahben149.fourthwall.utils.CurrencyUtils.formatCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val topUpBalanceUseCase: TopUpBalanceUseCase,
    private val getCurrencyAccountUseCase: GetCurrencyAccountUseCase,
    private val insertCurrencyAccountUseCase: InsertCurrencyAccountUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AccountState())
    val state: StateFlow<AccountState> = _state

    fun fundAccount(accountId: Int, amount: Double, currencyCode: String) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                topUpBalanceUseCase(accountId, amount)

                _state.update {
                    it.copy(
                        fundAccountProgress = FundAccountProgress.FundedSuccessfully(
                            amount.formatCurrency(currencyCode)
                                .plus(" has been added into your account")
                        )
                    )
                }
            } catch (ex: Exception) {
                _state.update {
                    it.copy(
                        fundAccountProgress = FundAccountProgress.ErrorFundingAccount(
                            "Error funding account. Please retry"
                        )
                    )
                }
            }
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

    fun setCurrency(currencyCode: String, countryFlag: Int?) {
        _state.update {
            it.copy(
                selectedCurrency = currencyCode,
                countryFlag = countryFlag,
                isCurrencySelected = true
            )
        }
    }

    fun createCurrencyAccount() {
        state.value.selectedCurrency?.let { currency ->
            viewModelScope.launch(Dispatchers.IO) {
                val currencyAccountEntity = CurrencyAccountEntity(
                    userId = sessionManager.getUserId(),
                    currencyCode = currency,
                    balance = 0.0,
                    isPrimaryAccount = if (state.value.isPrimaryAccount) 1 else 0
                )


                insertCurrencyAccountUseCase(currencyAccountEntity)
            }
        }
    }

}

data class AccountState(
    val isCurrencySelected: Boolean = false,
    val selectedCurrency: String? = null,
    val countryFlag: Int? = null,
    val isPrimaryAccount: Boolean = false,
    val fundAccountProgress: FundAccountProgress = FundAccountProgress.Default
)

sealed class FundAccountProgress {
    data object Default : FundAccountProgress()
    data class FundedSuccessfully(val amount: String) : FundAccountProgress()
    data class ErrorFundingAccount(val message: String) : FundAccountProgress()
}