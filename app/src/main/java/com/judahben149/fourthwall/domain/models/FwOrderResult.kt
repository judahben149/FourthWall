package com.judahben149.fourthwall.domain.models


data class FwOrderResult(
    val payInAmount: String,
    val payOutAmount: String,
    val payInCurrency: String,
    val payOutCurrency: String,
    val pfiDid: String,
    val orderStatus: Int
)
