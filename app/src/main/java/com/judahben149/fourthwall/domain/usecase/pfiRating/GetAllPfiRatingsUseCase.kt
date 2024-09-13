package com.judahben149.fourthwall.domain.usecase.pfiRating

import com.judahben149.fourthwall.data.repository.PfiRatingRepository
import com.judahben149.fourthwall.domain.mappers.toPfiRating
import com.judahben149.fourthwall.domain.models.PfiRating
import javax.inject.Inject

class GetAllPfiRatingsUseCase @Inject constructor(
    private val repository: PfiRatingRepository
) {
    suspend operator fun invoke(pfiDid: String): List<PfiRating> {
        return repository.getAllRatingsForPfi(pfiDid).map { it.toPfiRating() }
    }
}