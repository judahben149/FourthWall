package com.judahben149.fourthwall.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.judahben149.fourthwall.data.local.entities.CurrencyAccountEntity
import com.judahben149.fourthwall.data.local.entities.OrderEntity
import com.judahben149.fourthwall.data.local.entities.PfiRatingEntity
import com.judahben149.fourthwall.data.local.entities.UserAccountEntity

@Database(entities = [UserAccountEntity::class, CurrencyAccountEntity::class, OrderEntity::class, PfiRatingEntity::class], version = 1, exportSchema = false)
abstract class FourthWallDatabase: RoomDatabase() {

    abstract fun orderDao(): OrderDao
    abstract fun userAccountDao(): UserAccountDao
    abstract fun pfiRatingDao(): PfiRatingDao
}