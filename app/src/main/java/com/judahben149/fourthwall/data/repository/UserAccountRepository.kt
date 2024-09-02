package com.judahben149.fourthwall.data.repository

import com.judahben149.fourthwall.data.local.UserAccountDao
import com.judahben149.fourthwall.data.local.entities.CurrencyAccount
import com.judahben149.fourthwall.data.local.entities.UserAccount
import com.judahben149.fourthwall.data.local.relations.UserWithCurrencyAccounts
import com.judahben149.fourthwall.domain.models.PfiData
import com.judahben149.fourthwall.utils.PfiDataParser

class UserAccountRepository (
    private val userAccountDao: UserAccountDao,
    private val pfiDataParser: PfiDataParser
    ) {

    suspend fun insertUserAccount(userAccount: UserAccount) {
        userAccountDao.insertUserAccount(userAccount)
    }

    suspend fun insertCurrencyAccount(currencyAccount: CurrencyAccount) {
        userAccountDao.insertCurrencyAccount(currencyAccount)
    }

    suspend fun getUserWithCurrencyAccounts(userId: String): UserWithCurrencyAccounts? {
        return userAccountDao.getUserWithCurrencyAccounts(userId)
    }

    suspend fun getPfiData(): List<PfiData> {
        return pfiDataParser.parseJsonFromAssets()
    }
}