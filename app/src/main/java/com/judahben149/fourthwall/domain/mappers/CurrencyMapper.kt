package com.judahben149.fourthwall.domain.mappers

import com.judahben149.fourthwall.domain.models.Currency
import com.judahben149.fourthwall.utils.CurrencyUtils

fun String.toCurrency(): Currency? {
    CurrencyUtils.getCurrencyName(this)?.let { currencyName ->
        return Currency(this, currencyName)
    } ?: return null
}