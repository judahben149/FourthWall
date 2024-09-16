package com.judahben149.fourthwall.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pfi_ratings")
data class PfiRatingEntity(
    @PrimaryKey(autoGenerate = true) val ratingId: Int = 0,
    @ColumnInfo(name = "pfi_did") val pfiDid: String,
    @ColumnInfo(name = "rating") val rating: Double
)