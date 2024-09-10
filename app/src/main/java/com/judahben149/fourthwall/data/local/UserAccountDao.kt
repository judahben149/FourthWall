package com.judahben149.fourthwall.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.judahben149.fourthwall.data.local.entities.CurrencyAccountEntity
import com.judahben149.fourthwall.data.local.entities.UserAccountEntity
import com.judahben149.fourthwall.data.local.relations.UserWithCurrencyAccounts
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccount(userAccountEntity: UserAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyAccount(currencyAccountEntity: CurrencyAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyAccounts(currencyAccountEntities: List<CurrencyAccountEntity>)

    @Transaction
    @Query("SELECT * FROM user_accounts WHERE userId = :userId")
    fun getUserWithCurrencyAccounts(userId: Int): Flow<UserWithCurrencyAccounts?>

    @Query("UPDATE currency_accounts SET balance = balance + :amount WHERE id = :accountId")
    suspend fun topUpAccountBalance(accountId: Int, amount: Double)

    @Query("SELECT * FROM currency_accounts WHERE id = :accountId LIMIT 1")
    suspend fun getCurrencyAccountById(accountId: Int): CurrencyAccountEntity?
}