package com.judahben149.fourthwall.domain.usecase.orders

import com.judahben149.fourthwall.data.local.relations.UserWithCurrencyAccounts
import com.judahben149.fourthwall.data.repository.UserAccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserWithCurrencyAccountsUseCase @Inject constructor(
    private val userAccountRepository: UserAccountRepository
) {

    operator fun invoke(userId: Int): Flow<UserWithCurrencyAccounts?> =
        userAccountRepository.getUserWithCurrencyAccounts(userId)
}