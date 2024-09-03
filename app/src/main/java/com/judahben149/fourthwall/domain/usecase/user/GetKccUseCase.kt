package com.judahben149.fourthwall.domain.usecase.user

import com.judahben149.fourthwall.data.repository.CredentialsRepository
import javax.inject.Inject

class GetKccUseCase @Inject constructor(private val credentialsRepository: CredentialsRepository) {

    suspend operator fun invoke(
        name: String,
        countryCode: String,
        did: String
    ) = credentialsRepository.getKcc(name, countryCode, did)
}