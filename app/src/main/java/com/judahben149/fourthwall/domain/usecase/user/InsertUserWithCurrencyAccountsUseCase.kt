package com.judahben149.fourthwall.domain.usecase.user

import com.judahben149.fourthwall.data.local.relations.UserWithCurrencyAccounts
import com.judahben149.fourthwall.data.repository.UserAccountRepository
import javax.inject.Inject

class InsertUserWithCurrencyAccountsUseCase @Inject constructor(
    private val userAccountRepository: UserAccountRepository
) {

    suspend operator fun invoke(userWithCurrencyAccounts: UserWithCurrencyAccounts) {
        userAccountRepository.insertUserWithCurrencyAccounts(userWithCurrencyAccounts)
    }
}