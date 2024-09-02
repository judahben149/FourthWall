package com.judahben149.fourthwall.domain.usecase

import com.judahben149.fourthwall.data.repository.UserAccountRepository
import com.judahben149.fourthwall.domain.models.PfiData
import javax.inject.Inject

class GetPfiDataUseCase @Inject constructor(
    private val userAccountRepository: UserAccountRepository
) {

    suspend operator fun invoke(): List<PfiData> = userAccountRepository.getPfiData()
}