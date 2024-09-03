package com.judahben149.fourthwall.domain.usecase.pfi

import com.judahben149.fourthwall.data.repository.OfferingsRepository
import com.judahben149.fourthwall.domain.models.PfiData
import tbdex.sdk.protocol.models.Offering
import javax.inject.Inject

class GetPfiOfferingsUseCase @Inject constructor(private val offeringsRepository: OfferingsRepository) {

    suspend operator fun invoke(pfiData: PfiData): List<Offering> {
        return offeringsRepository.getPfiOfferings(pfiData)
    }
}