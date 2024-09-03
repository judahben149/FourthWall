package com.judahben149.fourthwall.domain.models

import androidx.annotation.DrawableRes

data class Country(
    val name: String,
    val currencyCode: String,
    @DrawableRes val flagResId: Int
)
