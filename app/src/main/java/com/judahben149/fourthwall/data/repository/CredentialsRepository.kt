package com.judahben149.fourthwall.data.repository

import com.judahben149.fourthwall.data.remote.VerifiableCredentialsService
import com.judahben149.fourthwall.data.remote.result.handleApi

class CredentialsRepository(private val vcService: VerifiableCredentialsService) {

    suspend fun getKcc(name: String, countryCode: String, did: String) =
        handleApi { vcService.getKcc(name, countryCode, did) }


}