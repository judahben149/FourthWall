package com.judahben149.fourthwall.utils

import com.judahben149.fourthwall.domain.models.CurrencyPair

object CurrencyUtils {

    private val supportedCurrencyPairs = listOf(
        CurrencyPair("GHS", "USDC"),
        CurrencyPair("NGN", "KES"),
        CurrencyPair("KES", "USD"),
        CurrencyPair("USD", "KES"),
        CurrencyPair("USD", "EUR"),
        CurrencyPair("EUR", "USD"),
        CurrencyPair("USD", "GBP"),
        CurrencyPair("USD", "BTC"),
        CurrencyPair("EUR", "USDC"),
        CurrencyPair("EUR", "GBP"),
        CurrencyPair("USD", "AUD"),
        CurrencyPair("USD", "MXN")
    )

    fun filterSupportedPairs(baseCurrency: String): List<CurrencyPair> {
        return supportedCurrencyPairs.filter { it.from == "USD" }
    }
}