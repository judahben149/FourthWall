package com.judahben149.fourthwall.domain.usecase.user

import com.judahben149.fourthwall.data.local.entities.CurrencyAccountEntity
import com.judahben149.fourthwall.data.repository.UserAccountRepository
import javax.inject.Inject

class InsertCurrencyAccountUseCase @Inject constructor(
    private val userAccountRepository: UserAccountRepository
) {

    suspend operator fun invoke(currencyAccountEntity: CurrencyAccountEntity) {
        userAccountRepository.insertCurrencyAccount(currencyAccountEntity)
    }
}