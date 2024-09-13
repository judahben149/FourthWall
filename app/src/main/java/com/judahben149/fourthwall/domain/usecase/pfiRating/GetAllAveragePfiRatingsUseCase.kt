package com.judahben149.fourthwall.domain.usecase.pfiRating

import com.judahben149.fourthwall.data.repository.PfiRatingRepository
import javax.inject.Inject

class GetAllAveragePfiRatingsUseCase @Inject constructor(private val repository: PfiRatingRepository) {
    suspend operator fun invoke(): Map<String, Double> {
        return repository.getAllPfiAverageRatings()
    }
}
