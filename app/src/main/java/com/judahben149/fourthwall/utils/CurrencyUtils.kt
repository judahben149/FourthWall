package com.judahben149.fourthwall.utils

import android.content.Context
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.domain.models.CurrencyPair
import com.murgupluoglu.flagkit.FlagKit
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

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

    fun getCurrencyName(currencyCode: String, locale: Locale = Locale.getDefault()): String? {
        return try {
            val currency = Currency.getInstance(currencyCode)
            currency.getDisplayName(locale)
        } catch (ex: Exception) {
            null
        }
    }

    private fun getCountryCode(currencyCode: String): String? {
        currencyToCountryMap[currencyCode]?.let { return it }

        return try {
            val currency = Currency.getInstance(currencyCode)
            val locale = Locale.getAvailableLocales().find {
                locale -> currency == Currency.getInstance(locale)
            }

            locale?.country
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun getCountryFlag(context: Context, currencyCode: String): Int? {
        if (currencyCode == "BTC") return R.drawable.ic_btc

        val countryCode = getCountryCode(currencyCode)
        return countryCode?.let { FlagKit.getResId(context, it) }
    }

    private val currencyToCountryMap: Map<String, String> = mapOf(
        "AED" to "AE",
        "AFN" to "AF",
        "ALL" to "AL",
        "AMD" to "AM",
        "ANG" to "CW",
        "AOA" to "AO",
        "ARS" to "AR",
        "AUD" to "AU",
        "AWG" to "AW",
        "AZN" to "AZ",
        "BAM" to "BA",
        "BBD" to "BB",
        "BDT" to "BD",
        "BGN" to "BG",
        "BHD" to "BH",
        "BIF" to "BI",
        "BMD" to "BM",
        "BND" to "BN",
        "BOB" to "BO",
        "BRL" to "BR",
        "BSD" to "BS",
        "BTN" to "BT",
        "BWP" to "BW",
        "BYN" to "BY",
        "BZD" to "BZ",
        "CAD" to "CA",
        "CDF" to "CD",
        "CHF" to "CH",
        "CLP" to "CL",
        "CNY" to "CN",
        "COP" to "CO",
        "CRC" to "CR",
        "CUP" to "CU",
        "CVE" to "CV",
        "CZK" to "CZ",
        "DJF" to "DJ",
        "DKK" to "DK",
        "DOP" to "DO",
        "DZD" to "DZ",
        "EGP" to "EG",
        "ERN" to "ER",
        "ETB" to "ET",
        "EUR" to "EU",
        "FJD" to "FJ",
        "FKP" to "FK",
        "GBP" to "GB",
        "GEL" to "GE",
        "GHS" to "GH",
        "GIP" to "GI",
        "GMD" to "GM",
        "GNF" to "GN",
        "GTQ" to "GT",
        "GYD" to "GY",
        "HKD" to "HK",
        "HNL" to "HN",
        "HRK" to "HR",
        "HTG" to "HT",
        "HUF" to "HU",
        "IDR" to "ID",
        "ILS" to "IL",
        "INR" to "IN",
        "IQD" to "IQ",
        "IRR" to "IR",
        "ISK" to "IS",
        "JMD" to "JM",
        "JOD" to "JO",
        "JPY" to "JP",
        "KES" to "KE",
        "KGS" to "KG",
        "KHR" to "KH",
        "KMF" to "KM",
        "KPW" to "KP",
        "KRW" to "KR",
        "KWD" to "KW",
        "KYD" to "KY",
        "KZT" to "KZ",
        "LAK" to "LA",
        "LBP" to "LB",
        "LKR" to "LK",
        "LRD" to "LR",
        "LSL" to "LS",
        "LYD" to "LY",
        "MAD" to "MA",
        "MDL" to "MD",
        "MGA" to "MG",
        "MKD" to "MK",
        "MMK" to "MM",
        "MNT" to "MN",
        "MOP" to "MO",
        "MRU" to "MR",
        "MUR" to "MU",
        "MVR" to "MV",
        "MWK" to "MW",
        "MXN" to "MX",
        "MYR" to "MY",
        "MZN" to "MZ",
        "NAD" to "NA",
        "NGN" to "NG",
        "NIO" to "NI",
        "NOK" to "NO",
        "NPR" to "NP",
        "NZD" to "NZ",
        "OMR" to "OM",
        "PAB" to "PA",
        "PEN" to "PE",
        "PGK" to "PG",
        "PHP" to "PH",
        "PKR" to "PK",
        "PLN" to "PL",
        "PYG" to "PY",
        "QAR" to "QA",
        "RON" to "RO",
        "RSD" to "RS",
        "RUB" to "RU",
        "RWF" to "RW",
        "SAR" to "SA",
        "SBD" to "SB",
        "SCR" to "SC",
        "SDG" to "SD",
        "SEK" to "SE",
        "SGD" to "SG",
        "SHP" to "SH",
        "SLL" to "SL",
        "SOS" to "SO",
        "SRD" to "SR",
        "SSP" to "SS",
        "STN" to "ST",
        "SVC" to "SV",
        "SYP" to "SY",
        "SZL" to "SZ",
        "THB" to "TH",
        "TJS" to "TJ",
        "TMT" to "TM",
        "TND" to "TN",
        "TOP" to "TO",
        "TRY" to "TR",
        "TTD" to "TT",
        "TWD" to "TW",
        "TZS" to "TZ",
        "UAH" to "UA",
        "UGX" to "UG",
        "USD" to "US",
        "UYU" to "UY",
        "UZS" to "UZ",
        "VES" to "VE",
        "VND" to "VN",
        "VUV" to "VU",
        "WST" to "WS",
        "XAF" to "CM",
        "XCD" to "AG",
        "XOF" to "BJ",
        "XPF" to "PF",
        "YER" to "YE",
        "ZAR" to "ZA",
        "ZMW" to "ZM",
        "ZWL" to "ZW"
    )

    fun Double.formatCurrency(currencyCode: String): String {
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        val currency = Currency.getInstance(currencyCode)
        format.currency = currency
        format.maximumFractionDigits = currency.defaultFractionDigits
        return format.format(this)
    }
}