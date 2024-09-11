package com.judahben149.fourthwall.domain.models

data class FWOffering(
    val id: Long,
    val offeringId: String,
    val pfiName: String,
    val pfiDid: String,
    val payInCurrency: String,
    val payOutCurrency: String,
    val payoutRate: String,
    val payInMethods: List<PaymentMethod>,
    val payOutMethods: List<PaymentMethod>,
)
