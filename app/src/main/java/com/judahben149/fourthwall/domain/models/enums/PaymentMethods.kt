package com.judahben149.fourthwall.domain.models.enums

/**
Hard coding this because the dynamic json schema in the requiredPaymentDetails field is not supported for
this hackathon
 */
enum class PaymentMethods {
    WALLET_ADDRESS,
    BANK_TRANSFER,
    BANK_TRANSFER_USD,
}