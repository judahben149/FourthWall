package com.judahben149.fourthwall.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.judahben149.fourthwall.data.local.entities.CurrencyAccount
import com.judahben149.fourthwall.data.local.entities.Order
import com.judahben149.fourthwall.data.local.entities.UserAccount

@Database(entities = [UserAccount::class, CurrencyAccount::class, Order::class], version = 1, exportSchema = false)
abstract class FourthWallDatabase: RoomDatabase() {

    abstract fun orderDao(): OrderDao
    abstract fun userAccountDao(): UserAccountDao
}