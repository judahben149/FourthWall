package com.judahben149.fourthwall.data.repository

import com.judahben149.fourthwall.data.local.UserAccountDao
import com.judahben149.fourthwall.data.local.entities.CurrencyAccount
import com.judahben149.fourthwall.data.local.entities.UserAccount
import com.judahben149.fourthwall.data.local.relations.UserWithCurrencyAccounts

class UserAccountRepository (private val userAccountDao: UserAccountDao) {

    suspend fun insertUserAccount(userAccount: UserAccount) {
        userAccountDao.insertUserAccount(userAccount)
    }

    suspend fun insertCurrencyAccount(currencyAccount: CurrencyAccount) {
        userAccountDao.insertCurrencyAccount(currencyAccount)
    }

    suspend fun getUserWithCurrencyAccounts(userId: String): UserWithCurrencyAccounts? {
        return userAccountDao.getUserWithCurrencyAccounts(userId)
    }
}