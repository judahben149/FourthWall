package com.judahben149.fourthwall.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.fourthwall.data.local.entities.CurrencyAccountEntity
import com.judahben149.fourthwall.data.local.entities.UserAccountEntity
import com.judahben149.fourthwall.data.local.relations.UserWithCurrencyAccounts
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.domain.usecase.user.GetKccUseCase
import com.judahben149.fourthwall.domain.usecase.user.GetUserWithCurrencyAccountsUseCase
import com.judahben149.fourthwall.domain.usecase.user.InsertUserAccountUseCase
import com.judahben149.fourthwall.domain.usecase.user.InsertUserWithCurrencyAccountsUseCase
import com.judahben149.fourthwall.utils.AndroidKeyManager
import com.judahben149.fourthwall.utils.Constants.BASE_USER_ID
import com.judahben149.fourthwall.utils.PasswordEncryptionUtil.encrypt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    private val insertUserAccountUseCase: InsertUserAccountUseCase,
    private val insertUserCurrencyUseCase: InsertUserWithCurrencyAccountsUseCase,
    private val getUserWithCurrencyAccountsUseCase: GetUserWithCurrencyAccountsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(UserRegistrationState())
    val state: StateFlow<UserRegistrationState> = _state.asStateFlow()


    fun signUp() {
        // Validate/verify email here
        // Mock it with delay

        viewModelScope.launch (Dispatchers.IO) {
            runningOperation()
            delay(1000)

            registerUserAccountInDatabase()

            _state.update {
                it.copy(
                    userLoginProgress = UserLoginProgress.SuccessSigningUp("Signed up successfully")
                )
            }
        }
    }

    fun getSignUpState() {
        viewModelScope.launch {
            val userAccount = getUserWithCurrencyAccountsUseCase(BASE_USER_ID).first()

            if (userAccount != null) {
                _state.update {
                    it.copy(userLoginProgress = UserLoginProgress.HasSignedUpButIsNotSignedIn)
                }
            } else {
                _state.update {
                    it.copy(userLoginProgress = UserLoginProgress.HasNotSignedUp)
                }
            }
        }
    }

    fun signIn() {
        viewModelScope.launch {
            runningOperation()
            val userAccount = getUserWithCurrencyAccountsUseCase(BASE_USER_ID).first()

            userAccount?.let { userCurrency ->
                val userEntity = userCurrency.userAccountEntity

                userEntity.let { it ->
                    val storedEmail = it.userEmail
                    val storedEncryptedPassword = it.userEncryptedPassword

                    val currentEmail = state.value.email
                    val currentEncryptedPassword = state.value.clearTextPassword.encrypt()

                    if (storedEmail == currentEmail) {
                        if (storedEncryptedPassword == currentEncryptedPassword) {
                            _state.update {
                                it.copy(
                                    userLoginProgress = UserLoginProgress.SuccessSigningIn("Sign in successful")
                                )
                            }
                        } else {
                            _state.update {
                                it.copy(
                                    userLoginProgress = UserLoginProgress.ErrorSigningIn("Incorrect password")
                                )
                            }
                        }
                    } else {
                        _state.update {
                            it.copy(
                                userLoginProgress = UserLoginProgress.ErrorSigningIn("Incorrect Email")
                            )
                        }
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

    private fun registerUserAccountInDatabase() {
        val userAccountEntity = UserAccountEntity(
            userId = BASE_USER_ID,
            userName = state.value.name,
            userEmail = state.value.email,
            userEncryptedPassword = state.value.clearTextPassword.encrypt(),
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

    fun registerUserInDatabase() {

        try {
            val userAccountEntity = UserAccountEntity(
                userId = BASE_USER_ID,
                userName = "",
                userCountryCode = "",
                userEmail = "",
                userEncryptedPassword = "",
            )

            viewModelScope.launch(Dispatchers.IO) {
                insertUserAccountUseCase(userAccountEntity)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun updateName(name: String) {
        _state.update {
            it.copy(name = name)
        }
    }

    fun updateEmail(email: String) {
        _state.update {
            it.copy(email = email)
        }
    }

    fun updatePassword(password: String) {
        _state.update {
            it.copy(clearTextPassword = password)
        }
    }

    fun updateCountryCode(countryCode: String) {
        _state.update {
            it.copy(countryCode = countryCode)
        }
    }

    fun updateCurrencyCode(currencyCode: String) {
        _state.update {
            it.copy(currencyCode = currencyCode)
        }
    }

    private fun runningOperation() {
        _state.update {
            it.copy(userLoginProgress = UserLoginProgress.RunningOperation)
        }
    }
}

data class UserRegistrationState(
    val name: String = "",
    val email: String = "",
    val clearTextPassword: String = "",
    val countryCode: String = "",
    val currencyCode: String = "",
    val userDid: String = "",
    val hasUserAgreed: Boolean = false,
    val userLoginProgress: UserLoginProgress = UserLoginProgress.HasNotSignedUp
)

sealed class UserLoginProgress {
    data object HasNotSignedUp : UserLoginProgress()
    data object HasSignedUpButIsNotSignedIn : UserLoginProgress()
    data object IsReadyToSignUp : UserLoginProgress()
    data object IsReadyToSignIn : UserLoginProgress()
    data object RunningOperation : UserLoginProgress()
    data class ErrorSigningUp(val message: String = "") : UserLoginProgress()
    data class ErrorSigningIn(val message: String = "") : UserLoginProgress()
    data class SuccessSigningUp(val message: String = "") : UserLoginProgress()
    data class SuccessSigningIn(val message: String = "") : UserLoginProgress()
}

