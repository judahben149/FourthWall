package com.judahben149.fourthwall.domain.usecase.pfiRating

import com.judahben149.fourthwall.data.repository.PfiRatingRepository
import javax.inject.Inject

class GetAveragePfiRatingUseCase @Inject constructor(
    private val repository: PfiRatingRepository
) {
    suspend operator fun invoke(pfiDid: String): Double {
        return repository.getAverageRatingForPfi(pfiDid)
    }
}