package com.judahben149.fourthwall.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.judahben149.fourthwall.data.local.entities.CurrencyAccount
import com.judahben149.fourthwall.data.local.entities.UserAccount
import com.judahben149.fourthwall.data.local.relations.UserWithCurrencyAccounts

@Dao
interface UserAccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccount(userAccount: UserAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyAccount(currencyAccount: CurrencyAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyAccounts(currencyAccounts: List<CurrencyAccount>)

    @Transaction
    @Query("SELECT * FROM user_accounts WHERE userId = :userId")
    suspend fun getUserWithCurrencyAccounts(userId: String): UserWithCurrencyAccounts?
}