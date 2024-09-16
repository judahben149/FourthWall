package com.judahben149.fourthwall.domain.usecase.pfiRating

import com.judahben149.fourthwall.data.repository.PfiRatingRepository
import com.judahben149.fourthwall.domain.models.PfiRating
import javax.inject.Inject

class InsertPfiRatingUseCase @Inject constructor(
    private val repository: PfiRatingRepository
) {
    suspend operator fun invoke(pfiRating: PfiRating) {
        repository.insertRating(pfiRating.pfiDid, pfiRating.pfiRating)
    }
}