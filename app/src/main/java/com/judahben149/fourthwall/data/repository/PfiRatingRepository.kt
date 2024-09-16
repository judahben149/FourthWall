package com.judahben149.fourthwall.data.repository

import com.judahben149.fourthwall.data.local.PfiRatingDao
import com.judahben149.fourthwall.data.local.entities.PfiRatingEntity
import javax.inject.Inject

class PfiRatingRepository @Inject constructor(
    private val pfiRatingDao: PfiRatingDao
) {
    suspend fun insertRating(pfiDid: String, rating: Double) {
        pfiRatingDao.insertRating(PfiRatingEntity(pfiDid = pfiDid, rating = rating))
    }

    suspend fun getAverageRatingForPfi(pfiDid: String): Double {
        return pfiRatingDao.getAverageRatingForPfi(pfiDid) ?: 0.0
    }

    suspend fun getAllRatingsForPfi(pfiDid: String): List<PfiRatingEntity> {
        return pfiRatingDao.getRatingsForPfi(pfiDid)
    }

    suspend fun getAllPfiAverageRatings(): Map<String, Double> {
        return pfiRatingDao.getAllPfiAverageRatings()
            .associate { it.pfiDid to (it.averageRating ?: 0.0) }
    }
}