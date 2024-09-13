package com.judahben149.fourthwall.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.judahben149.fourthwall.data.local.entities.PfiRatingEntity
import com.judahben149.fourthwall.domain.models.PfiAverageRating

@Dao
interface PfiRatingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRating(rating: PfiRatingEntity)

    @Query("SELECT * FROM pfi_ratings WHERE pfi_did = :pfiDid")
    suspend fun getRatingsForPfi(pfiDid: String): List<PfiRatingEntity>

    @Query("SELECT AVG(rating) FROM pfi_ratings WHERE pfi_did = :pfiDid")
    suspend fun getAverageRatingForPfi(pfiDid: String): Double?

    @Query("SELECT pfi_did, AVG(rating) as average_rating FROM pfi_ratings GROUP BY pfi_did")
    suspend fun getAllPfiAverageRatings(): List<PfiAverageRating>
}