package com.judahben149.fourthwall.domain.usecase.user

import com.judahben149.fourthwall.data.repository.UserAccountRepository
import javax.inject.Inject

class TopUpBalanceUseCase @Inject constructor(
    private val userAccountRepository: UserAccountRepository
) {

    suspend operator fun invoke(accountId: Int, amount: Double) {
        userAccountRepository.topUpAccountBalance(accountId, amount)
    }
}