package com.judahben149.fourthwall.domain.models

data class CurrencyAccount(
    val id: Int = 0,
    val userId: Int = 0,
    val currencyCode: String = "USD",
    val balance: Double = 0.0,
    val isPrimaryAccount: Int = 0
)
