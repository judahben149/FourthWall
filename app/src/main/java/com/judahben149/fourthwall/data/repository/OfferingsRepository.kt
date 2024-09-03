package com.judahben149.fourthwall.data.repository

import com.judahben149.fourthwall.domain.models.PfiData
import tbdex.sdk.httpclient.TbdexHttpClient
import tbdex.sdk.protocol.models.Offering
import javax.inject.Inject

class  OfferingsRepository @Inject constructor() {

    suspend fun getPfiOfferings(pfiData: PfiData): List<Offering> {
        return TbdexHttpClient.getOfferings(pfiData.pfiDid)
    }
}