package com.judahben149.fourthwall.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.judahben149.fourthwall.data.local.entities.CurrencyAccountEntity
import com.judahben149.fourthwall.data.local.entities.UserAccountEntity

data class UserWithCurrencyAccounts(
    @Embedded val userAccountEntity: UserAccountEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val currencyAccountEntities: List<CurrencyAccountEntity>
)