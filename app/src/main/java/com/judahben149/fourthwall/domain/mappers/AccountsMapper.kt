package com.judahben149.fourthwall.domain.mappers

import com.judahben149.fourthwall.data.local.entities.CurrencyAccountEntity
import com.judahben149.fourthwall.domain.models.CurrencyAccount

fun CurrencyAccountEntity.toCurrencyAccount(): CurrencyAccount {
    return CurrencyAccount(
        id = this.id,
        userId = this.userId,
        currencyCode = this.currencyCode,
        balance = this.balance,
        isPrimaryAccount = this.isPrimaryAccount
    )
}

fun CurrencyAccount.toCurrencyAccountEntity(): CurrencyAccountEntity {
    return CurrencyAccountEntity(
        id = this.id,
        userId = this.userId,
        currencyCode = this.currencyCode,
        balance = this.balance,
        isPrimaryAccount = this.isPrimaryAccount
    )
}