package com.judahben149.fourthwall.presentation.registration

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.fourthwall.data.local.entities.CurrencyAccountEntity
import com.judahben149.fourthwall.data.local.entities.UserAccountEntity
import com.judahben149.fourthwall.data.local.relations.UserWithCurrencyAccounts
import com.judahben149.fourthwall.data.remote.result.NetworkResult
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.domain.usecase.user.GetKccUseCase
import com.judahben149.fourthwall.domain.usecase.user.InsertUserWithCurrencyAccountsUseCase
import com.judahben149.fourthwall.utils.AndroidKeyManager
import com.judahben149.fourthwall.utils.Constants.BASE_USER_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import web5.sdk.dids.did.BearerDid
import web5.sdk.dids.methods.dht.DidDht
import javax.inject.Inject

@HiltViewModel
class UserRegistrationViewModel @Inject constructor(
    private val applicationContext: Context,
    private val getKccUseCase: GetKccUseCase,
    private val sessionManager: SessionManager,
    private val insertUserCurrencyUseCase: InsertUserWithCurrencyAccountsUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(UserRegistrationState())
    val state: StateFlow<UserRegistrationState> = _state.asStateFlow()

    fun getCredentials() {
        val name = state.value.name
        val countryCode = state.value.countryCode

        if (name.isEmpty()) {
            _state.update {
                it.copy(
                    credProgressState = CredentialsProgressState.Error(
                        "\"Name\" field cannot be empty"
                    )
                )
            }
            return
        }

        if (countryCode.isEmpty()) {
            _state.update {
                it.copy(
                    credProgressState = CredentialsProgressState.Error(
                        "Please select your country"
                    )
                )
            }
            return
        }

        setGetCredBtnLoading()

        viewModelScope.launch(Dispatchers.IO) {
            val did = createUserDid()

            when(val result = getKccUseCase(name, countryCode, did.uri)) {
                is NetworkResult.Error -> {
                    _state.update {
                        it.copy(
                            credProgressState = CredentialsProgressState.Error(
                                result.message ?: "Credentials were not setup. Please retry"
                            )
                        )
                    }

                    setContinueBtnEnabled()
                }

                is NetworkResult.Exception -> {
                    _state.update {
                        it.copy(
                            credProgressState = CredentialsProgressState.Error(
                                result.e.message ?: "Credentials were not setup. Please retry"
                            )
                        )
                    }

                    setContinueBtnEnabled()
                }

                is NetworkResult.Success -> {
                    registerUserInDatabase()
                    sessionManager.storeKCC(result.data)
                    // Store Did here

                    _state.update {
                        it.copy(
                            credProgressState = CredentialsProgressState.Success(
                                "Credential setup successful"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun createUserDid(): BearerDid {
        // Yeah, you can get your users DID here but you have to write mapper methods and use Gson to completely serialize and store it.
        // This is so you can easily retrieve it in any part of the app and reuse.

        val keyManager = AndroidKeyManager(applicationContext)
        return DidDht.create(keyManager)
    }

    private fun registerUserInDatabase() {
        val userAccountEntity = UserAccountEntity(
            userId = BASE_USER_ID,
            userName = state.value.name,
            userCountryCode = state.value.countryCode
        )

        val currencyAccountEntity = CurrencyAccountEntity(
            userId = BASE_USER_ID,
            currencyCode = state.value.currencyCode,
            balance = 0.0,
            isPrimaryAccount = 1
        )

        val currencyAccounts = listOf(currencyAccountEntity)
        val userWithCurrencyAccounts = UserWithCurrencyAccounts(userAccountEntity, currencyAccounts)

        try {
            viewModelScope.launch(Dispatchers.IO) {
                insertUserCurrencyUseCase(userWithCurrencyAccounts)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun setGetCredBtnLoading() {
        _state.update {
            it.copy(getCredBtnState = GetCredentialsButtonState.Loading)
        }
    }

    private fun setContinueBtnEnabled(message: String = "") {
        _state.update {
            it.copy(continueBtnState = ContinueButtonState.Enabled(message))
        }
    }

    private fun setContinueBtnDisabled(error: String = "") {
        _state.update {
            it.copy(continueBtnState = ContinueButtonState.Disabled(error))
        }
    }

    fun updateName(name: String) {
        _state.update {
            it.copy(name = name)
        }

        evaluateContinueBtnStatus()
    }

    fun updateCountryCode(countryCode: String) {
        _state.update {
            it.copy(countryCode = countryCode)
        }

        evaluateContinueBtnStatus()
    }

    fun updateCurrencyCode(currencyCode: String) {
        _state.update {
            it.copy(currencyCode = currencyCode)
        }

        evaluateContinueBtnStatus()
    }

    fun toggleUserAgree(hasAgreed: Boolean) {
        _state.update {
            it.copy(hasUserAgreed = hasAgreed)
        }

        evaluateGetCredBtnStatus()
    }

    private fun evaluateContinueBtnStatus() {
        if (state.value.name.isEmpty() || state.value.countryCode.isEmpty()) {
            setContinueBtnDisabled()
        } else {
            setContinueBtnEnabled()
        }
    }

    private fun evaluateGetCredBtnStatus() {
        if (state.value.hasUserAgreed) {
            setGetCredButtonEnabled()
        } else {
            setGetCredButtonDisabled()
        }
    }

    private fun setGetCredButtonEnabled(message: String = "") {
        _state.update {
            it.copy(getCredBtnState = GetCredentialsButtonState.Enabled(message))
        }
    }

    private fun setGetCredButtonDisabled(error: String = "") {
        _state.update {
            it.copy(getCredBtnState = GetCredentialsButtonState.Disabled(error))
        }
    }
}

data class UserRegistrationState(
    val name: String = "",
    val countryCode: String = "",
    val currencyCode: String = "",
    val userDid: String = "",
    val hasUserAgreed: Boolean = false,
    val continueBtnState: ContinueButtonState = ContinueButtonState.Disabled(),
    val getCredBtnState: GetCredentialsButtonState = GetCredentialsButtonState.Disabled(),
    val credProgressState: CredentialsProgressState = CredentialsProgressState.Default
)

sealed class ContinueButtonState {
    data class Enabled(val message: String = ""): ContinueButtonState()
    data class Disabled(val error: String = ""): ContinueButtonState()
}

sealed class GetCredentialsButtonState {
    data object Loading: GetCredentialsButtonState()
    data class Enabled(val message: String = ""): GetCredentialsButtonState()
    data class Disabled(val error: String = ""): GetCredentialsButtonState()
}

sealed class CredentialsProgressState {
    data object InProgress : CredentialsProgressState()
    data object Default : CredentialsProgressState()
    data class Error(val message: String = "") : CredentialsProgressState()
    data class Success(val message: String = "") : CredentialsProgressState()
}

