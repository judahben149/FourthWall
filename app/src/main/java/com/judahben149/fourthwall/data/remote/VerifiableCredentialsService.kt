package com.judahben149.fourthwall.data.remote

import com.judahben149.fourthwall.utils.Constants.KCC_ENDPOINT
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VerifiableCredentialsService {

    @GET(KCC_ENDPOINT)
    suspend fun getKcc(
        @Query("name") name: String,
        @Query("country") countryCode: String,
        @Query("did") did: String
    ): Response<String>
}