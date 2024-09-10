package com.judahben149.fourthwall.domain.usecase.user

import com.judahben149.fourthwall.data.local.entities.CurrencyAccountEntity
import com.judahben149.fourthwall.data.repository.UserAccountRepository
import javax.inject.Inject

class GetCurrencyAccountUseCase @Inject constructor(
    private val userAccountRepository: UserAccountRepository
) {

    suspend operator fun invoke(accountId: Int): CurrencyAccountEntity? {
        return userAccountRepository.getCurrencyAccount(accountId)
    }
}