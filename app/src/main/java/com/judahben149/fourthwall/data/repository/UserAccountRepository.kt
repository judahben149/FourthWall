package com.judahben149.fourthwall.data.repository

import com.judahben149.fourthwall.data.local.UserAccountDao
import com.judahben149.fourthwall.data.local.entities.CurrencyAccountEntity
import com.judahben149.fourthwall.data.local.entities.UserAccountEntity
import com.judahben149.fourthwall.data.local.relations.UserWithCurrencyAccounts
import com.judahben149.fourthwall.domain.models.PfiData
import com.judahben149.fourthwall.utils.text.PfiDataParser
import kotlinx.coroutines.flow.Flow

class UserAccountRepository (
    private val userAccountDao: UserAccountDao,
    private val pfiDataParser: PfiDataParser
    ) {

    suspend fun insertUserAccount(userAccountEntity: UserAccountEntity) {
        userAccountDao.insertUserAccount(userAccountEntity)
    }

    suspend fun insertCurrencyAccount(currencyAccountEntity: CurrencyAccountEntity) {
        userAccountDao.insertCurrencyAccount(currencyAccountEntity)
    }

    suspend fun insertUserWithCurrencyAccounts(userWithCurrencyAccounts: UserWithCurrencyAccounts) {
        userAccountDao.insertUserAccount(userWithCurrencyAccounts.userAccountEntity)
        userAccountDao.insertCurrencyAccounts(userWithCurrencyAccounts.currencyAccountEntities)
    }

    fun getUserWithCurrencyAccounts(userId: Int): Flow<UserWithCurrencyAccounts?> {
        return userAccountDao.getUserWithCurrencyAccounts(userId)
    }

    suspend fun getPfiData(): List<PfiData> {
        return pfiDataParser.parseJsonFromAssets()
    }
}