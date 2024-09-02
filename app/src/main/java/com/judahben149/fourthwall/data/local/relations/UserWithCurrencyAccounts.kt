package com.judahben149.fourthwall.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.judahben149.fourthwall.data.local.entities.CurrencyAccount
import com.judahben149.fourthwall.data.local.entities.UserAccount

data class UserWithCurrencyAccounts(
    @Embedded val userAccount: UserAccount,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val currencyAccounts: List<CurrencyAccount>
)