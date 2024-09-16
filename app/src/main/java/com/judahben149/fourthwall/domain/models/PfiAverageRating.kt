package com.judahben149.fourthwall.domain.models

import androidx.room.ColumnInfo

data class PfiAverageRating(
    @ColumnInfo(name = "pfi_did") val pfiDid: String,
    @ColumnInfo(name = "average_rating") val averageRating: Double
)