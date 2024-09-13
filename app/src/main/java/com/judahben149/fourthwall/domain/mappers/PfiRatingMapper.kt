package com.judahben149.fourthwall.domain.mappers

import com.judahben149.fourthwall.data.local.entities.PfiRatingEntity
import com.judahben149.fourthwall.domain.models.PfiRating

fun PfiRatingEntity.toPfiRating(): PfiRating {
    return PfiRating(
        ratingId = ratingId,
        pfiDid = pfiDid,
        pfiRating = rating
    )
}

fun PfiRating.toPfiRatingEntity(): PfiRatingEntity {
    return PfiRatingEntity(
        ratingId = ratingId,
        pfiDid = pfiDid,
        rating = pfiRating
    )
}