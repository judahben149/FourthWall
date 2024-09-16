package com.judahben149.fourthwall.domain.usecase.user

import com.judahben149.fourthwall.data.local.entities.UserAccountEntity
import com.judahben149.fourthwall.data.repository.UserAccountRepository
import javax.inject.Inject

class InsertUserAccountUseCase @Inject constructor(
    private val userAccountRepository: UserAccountRepository
) {

    suspend operator fun invoke(userAccountEntity: UserAccountEntity) {
        userAccountRepository.insertUserAccount(userAccountEntity)
    }
}